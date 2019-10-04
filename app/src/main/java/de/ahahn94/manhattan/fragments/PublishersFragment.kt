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
import de.ahahn94.manhattan.activities.FragmentedActivity
import de.ahahn94.manhattan.adapters.PublishersAdapter
import de.ahahn94.manhattan.viewModels.PublishersViewModel

/**
 * Class to handle the publishers fragment.
 */
class PublishersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: PublishersViewModel

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

        // Bind recyclerView.
        recyclerView = view.findViewById(R.id.recycler)

        // Get viewModel that contains the data for the fragment.
        viewModel = ViewModelProviders.of(this).get(PublishersViewModel::class.java)

        // Bind the data from viewModel to the recyclerView.
        bindData()

        return view
    }

    /**
     * Bind data to the recyclerView.
     * This makes the displayed list auto-update on changes to the database.
     */
    private fun bindData() {
        val list = viewModel.publishers
        val adapter = PublishersAdapter(fragmentManager!!, activity as FragmentedActivity, list)

        // Set observer.
        list.observe(
            this,
            Observer { changedList -> adapter.submitList(changedList) })

        this.recyclerView.adapter = adapter
    }

}
