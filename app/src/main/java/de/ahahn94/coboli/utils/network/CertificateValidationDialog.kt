package de.ahahn94.coboli.utils.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import de.ahahn94.coboli.R
import de.ahahn94.coboli.utils.replaceNull
import de.ahahn94.coboli.utils.security.KnownServers
import java.security.cert.X509Certificate

/**
 * Class that handles the certificate validation dialog.
 */
class CertificateValidationDialog :
    DialogFragment() {

    // Certificate details (for display) and certificate (for adding to the known servers).
    private lateinit var certificateDetails: CertificateDetails
    private lateinit var certificate: X509Certificate

    companion object {

        // Constants.
        const val TAG = "CertificateValidationDialog"

        /**
         * Parse a certificate principal string into a map of its parts.
         * Returns a map that maps the keys (like CN) to their values (like example.com).
         */
        fun parsePrincipal(principalName: String): Map<String, String> {
            val stringParts = principalName.split(",")
            val pairs = stringParts.map {
                val parts = it.split("=")
                parts.first() to parts.drop(1).joinToString("")
            }
            return pairs.toMap()
        }

    }

    /**
     * onCreate function.
     * Customizations:
     * Set custom style/theme.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme)
    }

    /**
     * onCreateView function.
     * Customizations:
     * Inflate custom layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_trust_certificate, container, false)
    }

    /**
     * onViewCreated function.
     * Customizations:
     * Fill TextViews with values.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Assure that view is initialized before trying to access the TextViews.
        fillDialog(certificateDetails)
    }

    /**
     * Get the certificate and extract the details for the dialog.
     * Returns the certificate details or an empty CertificateDetails if certificate is null.
     */
    fun loadCertificateDetails() {
        val cert = SslValidation.getCertificate()

        certificateDetails = if (cert != null) {
            certificate = cert
            // Set layout and fill in cert details.
            val issuer = parsePrincipal(certificate.issuerX500Principal.name)
            val subject = parsePrincipal(certificate.subjectX500Principal.name)
            CertificateDetails(
                subject["CN"] replaceNull "",
                subject["O"] replaceNull "",
                subject["OU"] replaceNull "",
                issuer["CN"] replaceNull "",
                issuer["O"] replaceNull "",
                issuer["OU"] replaceNull "",
                certificate.notBefore.toString(),
                certificate.notAfter.toString()
            )
        } else CertificateDetails()
    }

    /**
     * Fill the dialog with the details from certificateDetails.
     * Set the listeners for the yes and no buttons.
     */
    private fun fillDialog(certificateDetails: CertificateDetails) {

        val yesListener = OnClickListener {
            run {
                // Save certificate to known servers and close dialog.
                KnownServers.saveCertificate(certificate)
                dismiss()
            }
        }

        val noListener = OnClickListener {
            run {
                // Close dialog.
                dismiss()
            }
        }

        // Fill TextViews with certificate details.

        this.view?.findViewById<TextView>(R.id.text_issued_to_cn)?.text =
            certificateDetails.subjectCN
        this.view?.findViewById<TextView>(R.id.text_issued_to_o)
            ?.text = certificateDetails.subjectO
        this.view?.findViewById<TextView>(R.id.text_issued_to_ou)?.text =
            certificateDetails.subjectOU

        this.view?.findViewById<TextView>(R.id.text_issued_by_cn)?.text =
            certificateDetails.issuerCN
        this.view?.findViewById<TextView>(R.id.text_issued_by_o)?.text =
            certificateDetails.issuerO
        this.view?.findViewById<TextView>(R.id.text_issued_by_ou)?.text =
            certificateDetails.issuerOU

        this.view?.findViewById<TextView>(R.id.text_valid_from)?.text =
            certificateDetails.validFrom
        this.view?.findViewById<TextView>(R.id.text_label_until)?.text =
            certificateDetails.validUntil

        this.view?.findViewById<Button>(R.id.button_no)?.setOnClickListener(noListener)
        this.view?.findViewById<Button>(R.id.button_yes)?.setOnClickListener(yesListener)

    }

}

/**
 * Data class for details from certificates.
 * Fields may be empty strings, as certificate fields are optional.
 */
data class CertificateDetails(
    val subjectCN: String = "",
    val subjectO: String = "",
    val subjectOU: String = "",
    val issuerCN: String = "",
    val issuerO: String = "",
    val issuerOU: String = "",
    val validFrom: String = "",
    val validUntil: String = ""
)