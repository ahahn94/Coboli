package de.ahahn94.coboli.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.coboli.R
import de.ahahn94.coboli.adapters.LibrariesAdapter
import de.ahahn94.coboli.viewModels.AboutViewModel
import java.util.*

/**
 * Class to handle the "About" section.
 * Shows information about Coboli and its libraries.
 */
class AboutFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: AboutViewModel

    /**
     * OnCreateView-function.
     * Customizations:
     * - Load layout
     * - Init ViewModel
     * - Bind data to RecyclerView.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Load fragment layout.
        val view = layoutInflater.inflate(R.layout.fragment_about, container, false)

        // Bind recyclerView.
        recyclerView = view.findViewById(R.id.recycler_libraries)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Get viewModel that contains the data for the fragment.
        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)

        // Bind the copyright notice and license button.
        val copyrightLabel = view.findViewById<TextView>(R.id.label_copyright)
        val licenseButton = view.findViewById<TextView>(R.id.button_license)
        val websiteButton = view.findViewById<TextView>(R.id.button_website)

        // Add data to copyrightLabel and buttons.
        viewModel.app.observe(this, Observer { changedAppInfo ->
            copyrightLabel.text = changedAppInfo.copyright

            licenseButton.setOnClickListener {
                // Show LicenseFragment as a dialog.
                val dialog = LicenseFragment()
                val bundle = Bundle()
                bundle.putString(LicenseFragment.LICENSE_TEXT, changedAppInfo.licenseText)
                dialog.arguments = bundle
                val transaction = fragmentManager!!.beginTransaction()
                dialog.show(transaction, LicenseFragment.TAG)
            }

            // Set OnClickListener on the button_website to open the project website in a web browser.
            websiteButton.setOnClickListener {
                val website = changedAppInfo?.website
                if (website != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
                    try {
                        startActivity(intent)
                    } catch (exception: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            getString(R.string.toast_broken_url),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        // Bind the data from viewModel to the recyclerView.
        bindData()

        return view
    }

    /**
     * Bind data to the recyclerView.
     * This makes the displayed list auto-update on changes to the database.
     */
    private fun bindData() {
        val list = viewModel.libraries
        val adapter = LibrariesAdapter(fragmentManager!!, this.context!!)

        // Set observer.
        list.observe(
            this,
            Observer { changedList ->
                adapter.submitList(changedList.sortedBy { it.name.toLowerCase(Locale.getDefault()) })
            })

        this.recyclerView.adapter = adapter
    }

}
