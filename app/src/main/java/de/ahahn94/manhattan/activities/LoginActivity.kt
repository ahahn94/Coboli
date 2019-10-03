package de.ahahn94.manhattan.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.autofill.AutofillManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.utils.network.CertificateValidationDialog
import de.ahahn94.manhattan.utils.network.ConnectionStatus
import de.ahahn94.manhattan.utils.network.ConnectionStatusType.*
import de.ahahn94.manhattan.utils.network.ConnectionTester
import de.ahahn94.manhattan.utils.settings.Credentials
import de.ahahn94.manhattan.utils.settings.Preferences
import java.lang.ref.WeakReference
import java.util.*

/**
 * Class that handles the login activity.
 */
class LoginActivity : AppCompatActivity() {

    companion object {

        // Constants.
        private const val SYNC_REQUEST_CODE = 1

    }

    // AutofillManager and Credentials for password autofill and saving.
    private lateinit var autofillManager: AutofillManager
    private lateinit var credentials: Credentials

    // Text fields.
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputServerAddress: EditText

    /**
     * onCreate function.
     * Customizations:
     * Load layout.
     * Get credentials from preferences.
     * Init AutofillManager if SDK-version > O.
     * Bind EditTexts to variables.
     * Autofill server address if in preferences.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        credentials = Credentials.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            autofillManager = getSystemService(AutofillManager::class.java) as AutofillManager
        }

        inputUsername = findViewById(R.id.LoginUsername)
        inputPassword = findViewById(R.id.LoginPassword)
        inputServerAddress = findViewById(R.id.LoginServerAddress)

        // If server address in settings, autofill address.
        val serverAddressFromPreferences =
            Preferences.getInstance().getString(Preferences.SERVER_ADDRESS_KEY, "")
        if (serverAddressFromPreferences != "") {
            inputServerAddress.setText(serverAddressFromPreferences)
        }


    }

    /**
     * OnClick-Function of the LoginButton.
     * Get content from EditTexts, save server address to preferences and check connection.
     */
    fun loginButtonClicked(view: View) {
        // Get input.
        val username = inputUsername.text.toString().trim()
        val password = inputPassword.text.toString().trim()
        val serverAddress = inputServerAddress.text.toString().trim()

        // Show a warning message if trying to use HTTP instead of HTTPS.
        // User may proceed with HTTP or cancel to change the server address.
        if (serverAddress.split(":").first().toLowerCase(Locale.getDefault()) == "http") {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle(getString(R.string.http_warning_title))
                .setMessage(R.string.http_warning_message)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    testCredentials(username, password, serverAddress)
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }
            val dialog = dialogBuilder.create()
            dialog.show()
        } else {
            // If HTTPS, just test the credentials.
            testCredentials(username, password, serverAddress)
        }
    }

    /**
     * Test if the credentials and server address are valid.
     */
    private fun testCredentials(username: String, password: String, serverAddress: String) {
        // Update credentials with input.
        credentials.username = username
        credentials.password = password

        // Update server address with input.
        Preferences.putString(Preferences.SERVER_ADDRESS_KEY, serverAddress)

        // Check connection in an AsyncTask.
        ConnectionChecker(WeakReference(this)).execute()
    }

    /**
     * Show the ConnectionCheckers result.
     * Show Toast, get API key, save credentials and Autofill and close activity if successfully connected.
     * Show AlertDialog if parameter error to notify user about error in EditTexts.
     * Show CertificateValidationDialog if certificate error to allow user to review certificate details and add the
     * certificate to the known servers.
     */
    fun showConnectionCheckerResult(connectionStatus: ConnectionStatus) {

        when (connectionStatus.statusType) {
            OK -> {
                // Successfully connected and logged in.
                val toast: Toast = Toast.makeText(this, connectionStatus.message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                toast.show()

                // Get api key from response.
                val token = connectionStatus.response?.body()
                if (token != null) {
                    val apiKey = token.responseContent?.apiKey
                    Credentials.getInstance().apiKey = apiKey.toString()
                }

                // Save credentials
                Credentials.saveInstance()
                // Commit autofill credentials.
                commitAutofillCredentials()

                // Sync database with ComicLib server. Shows a new activity.
                startActivityForResult(Intent(this, SyncActivity::class.java), SYNC_REQUEST_CODE)
            }
            PARAM_ERROR ->
                // Show AlertDialog with error message.
                AlertDialog.Builder(this).setMessage(connectionStatus.message).setNeutralButton(
                    "OK"
                ) { dialog, _ -> dialog.dismiss() }.show()
            SSL_ERROR -> {
                // Ask if the invalid certificate and the server can be trusted.
                val connectionValidationDialog = CertificateValidationDialog()
                DialogStarter(
                    connectionValidationDialog,
                    WeakReference(this)
                ).execute()
            }
        }

    }

    /**
     * Handle results from activities created by this class.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SYNC_REQUEST_CODE -> {
                // Sync finished. Close LoginActivity.
                finish()
            }
        }
    }

    /**
     * Commit the changes to the credentials to the AutofillManager.
     * Enables credentials autofill for the next login inside the app.
     * Only SDK-versions > O.
     */
    private fun commitAutofillCredentials() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            autofillManager.commit()
        }
    }

    /**
     * Go back to the home screen if the back button is pressed.
     */
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    /**
     * AsyncTask to check the connection in the background and show the result after that via callback.
     */
    class ConnectionChecker(private val loginActivity: WeakReference<LoginActivity>) :
        AsyncTask<String, Int, ConnectionStatus>() {

        override fun doInBackground(vararg params: String?): ConnectionStatus {
            return ConnectionTester.test()
        }

        override fun onPostExecute(result: ConnectionStatus) {
            loginActivity.get()?.showConnectionCheckerResult(result)
        }

    }

    /**
     * AsyncTask to show the CertificateValidationDialog.
     * Loads the certificate details in the background and shows the dialog after that via callback.
     */
    class DialogStarter(
        private val dialog: CertificateValidationDialog,
        private val loginActivity: WeakReference<LoginActivity>
    ) : AsyncTask<String, Int, Unit>() {

        override fun doInBackground(vararg params: String?) {
            dialog.loadCertificateDetails()
        }

        override fun onPostExecute(result: Unit?) {
            val transaction = loginActivity.get()?.supportFragmentManager?.beginTransaction()
            dialog.show(transaction!!, CertificateValidationDialog.TAG)
        }
    }

}