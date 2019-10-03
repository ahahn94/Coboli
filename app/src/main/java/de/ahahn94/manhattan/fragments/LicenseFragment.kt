package de.ahahn94.manhattan.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import de.ahahn94.manhattan.R

/**
 * Class to handle the dialog fragment for license texts.
 */
class LicenseFragment : DialogFragment() {

    companion object {

        // Constants.
        const val TAG = "LicenseFragment"

        // Bundle-IDs for the values passed into the fragment at creation.
        const val LICENSE_TEXT = "licenseText"
    }

    /**
     * OnCreateView-function.
     * Customizations:
     * - Load layout
     * - Bind data to views.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Load fragment layout.
        val view = layoutInflater.inflate(R.layout.fragment_license, container, false)

        // Get license text.
        val licenseText = arguments?.getString(LICENSE_TEXT) ?: ""

        val textViewLicense = view.findViewById<TextView>(R.id.text_license)
        textViewLicense.text = licenseText

        return view
    }

    /**
     * onViewCreated function.
     * Customizations:
     * Make background transparent (so the fragment acts as an overlay on the collection).
     * Add OnClickListener to the close-button.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Make background transparent.
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Add OnClickListener to the closeButton. Makes the closeButton close the DialogFragment.
        view.findViewById<ImageView>(R.id.button_close).setOnClickListener {
            dismiss()
        }
    }


    /**
     * onStart function.
     * Modified to make fragment fill most of the screen.
     */
    override fun onStart() {
        super.onStart()
        // Make layout size match parent.
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}
