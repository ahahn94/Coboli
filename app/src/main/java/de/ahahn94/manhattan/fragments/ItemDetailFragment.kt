package de.ahahn94.manhattan.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.asynctasks.ImagesLoader
import java.lang.ref.WeakReference

/**
 * Class that handles the details of collection items.
 */
class ItemDetailFragment() :
    DialogFragment() {

    private var imageFilePath: String = ""
    private var name: String = ""
    private var description: String = ""

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
        val view = inflater.inflate(R.layout.fragment_item_detail, container, false)
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
        fillView()
    }

    /**
     * onStart function.
     * Modified to make fragment fill most of the screen.
     */
    override fun onStart() {
        super.onStart()
        // Make layout size match parent.
        dialog?.window?.setLayout(
            MATCH_PARENT,
            MATCH_PARENT
        )
    }

    /**
     * Update the content of the fragment.
     * Called from within the object that creates the fragment.
     */
    fun updateContent(imageFilePath: String, name: String, description: String) {
        this.imageFilePath = imageFilePath
        this.name = name
        this.description = description
    }

    /**
     * Fill the view of the fragment with the content.
     */
    private fun fillView() {
        val imageProgress = view?.findViewById<ProgressBar>(R.id.progress_image)
        val imageView = view?.findViewById<ImageView>(R.id.image_detail)
        val nameTextView = view?.findViewById<TextView>(R.id.label_name)
        val descriptionTextView = view?.findViewById<TextView>(R.id.text_description)

        // Load image in background.
        if (imageView != null && imageProgress != null) {
            ImagesLoader(
                imageFilePath,
                WeakReference(imageView),
                WeakReference(imageProgress)
            ).execute()
        }

        nameTextView?.text = name

        /**
         * Make the description - which probably contains HTML-tags - look right.
         * Clickable links, text formatting, etc.
         */
        descriptionTextView?.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(description)
        }
        descriptionTextView?.movementMethod = LinkMovementMethod.getInstance()
    }

}