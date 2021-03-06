package de.ahahn94.coboli.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.coboli.R
import de.ahahn94.coboli.activities.FragmentedActivity
import de.ahahn94.coboli.adapters.VolumesAdapter
import de.ahahn94.coboli.viewModels.VolumesViewModel

/**
 * Class to handle the volumes fragment.
 */
class VolumesFragment : Fragment() {

    companion object {

        // Bundle-IDs for the values passed into the fragment at creation.
        const val PUBLISHER_ID_NAME = "publisherID"
        const val CACHED_VOLUMES = "cachedVolumes"
        const val SEARCH_QUERY = "searchQuery"

    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: VolumesViewModel

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
        val view = layoutInflater.inflate(R.layout.fragment_recycler, container, false)

        // Get publisherID.
        val publisherID = arguments?.getString(PUBLISHER_ID_NAME) ?: ""

        // Only cached volumes?
        val cachedOnly = arguments?.getBoolean(CACHED_VOLUMES, false) ?: false

        // Get search query.
        val searchQuery = arguments?.getString(SEARCH_QUERY) ?: ""

        // Bind recyclerView.
        recyclerView = view.findViewById(R.id.recycler)

        // Get viewModel that contains the data for the fragment.
        viewModel = ViewModelProviders.of(
            this,
            VolumesViewModel.Factory(publisherID, cachedOnly, searchQuery)
        ).get(VolumesViewModel::class.java)

        // Bind the data from viewModel to the recyclerView.
        bindData(cachedOnly)

        return view
    }

    /**
     * Bind data to the recyclerView.
     * This makes the displayed list auto-update on changes to the database.
     */
    private fun bindData(cachedOnly: Boolean) {
        // Bind data to recyclerView.
        val list = viewModel.volumes
        val adapter = VolumesAdapter(
            fragmentManager!!,
            activity as FragmentedActivity, cachedOnly, list
        )

        // Set observer.
        list.observe(
            this, Observer { changedList -> adapter.submitList(changedList) })

        recyclerView.adapter = adapter
    }

}
