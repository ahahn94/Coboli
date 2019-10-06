package de.ahahn94.coboli.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import de.ahahn94.coboli.R
import de.ahahn94.coboli.adapters.PagesOverviewAdapter
import de.ahahn94.coboli.livedata.ThumbnailsLiveData
import java.lang.ref.WeakReference

/**
 * Class that handles the page thumbnails overview of an issue.
 */
class PagesOverviewFragment(
    private val thumbnails: ThumbnailsLiveData,
    private val position: Int,
    private val viewPager: WeakReference<ViewPager>
) :
    DialogFragment() {

    private lateinit var recyclerView: RecyclerView

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
        // Load fragment layout.
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)

        // Bind recyclerView.
        recyclerView = view.findViewById(R.id.recycler)

        recyclerView.layoutManager = GridLayoutManager(context, 4)

        // Bind data to recyclerView.
        val adapter = PagesOverviewAdapter(position, viewPager, this)

        // Observe thumbnails (LiveData) and update the adapters on change.
        thumbnails.observe(this, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        this.recyclerView.adapter = adapter

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

}