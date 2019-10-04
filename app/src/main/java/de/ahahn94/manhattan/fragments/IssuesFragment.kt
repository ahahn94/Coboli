package de.ahahn94.manhattan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.adapters.IssuesAdapter
import de.ahahn94.manhattan.viewModels.IssuesViewModel
import java.lang.ref.WeakReference

/**
 * Class to handle the issues fragment.
 */
class IssuesFragment : Fragment() {

    companion object {

        // Bundle-IDs for values passed into the fragment at creation.
        const val VOLUME_ID_NAME = "volumeID"
        const val CACHED_ISSUES = "cachedIssues"
        const val READING_LIST = "readingList"

    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: IssuesViewModel

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
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)

        // Get volumeID.
        val volumeID = arguments?.getString(VOLUME_ID_NAME) ?: ""

        // Only cached issues?
        val cachedOnly = arguments?.getBoolean(CACHED_ISSUES, false) ?: false

        // Get the reading list?
        val readingList = arguments?.getBoolean(READING_LIST, false) ?: false

        // Bind recyclerView.
        recyclerView = view.findViewById(R.id.recycler)

        // Get viewModel that contains the data for the fragment.
        viewModel = ViewModelProviders.of(
            this,
            IssuesViewModel.Factory(volumeID, cachedOnly, readingList)
        ).get(IssuesViewModel::class.java)

        bindData()

        return view
    }

    /**
     * Bind data to the recyclerView and the cardView.
     * This makes the displayed data auto-update on changes to the database.
     */
    private fun bindData() {

        // Bind data to recyclerView.
        val list = viewModel.issues
        val adapter = IssuesAdapter(WeakReference(fragmentManager!!), list)

        // Set observer.
        list.observe(
            this,
            Observer { changedList ->
                adapter.submitList(changedList)
            })

        this.recyclerView.adapter = adapter
    }

}
