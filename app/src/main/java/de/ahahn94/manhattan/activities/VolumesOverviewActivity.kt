package de.ahahn94.manhattan.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.adapters.VolumesAdapter
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.viewModels.VolumesOverviewViewModel

/**
 * Class to handle the volumes overview activity.
 */
class VolumesOverviewActivity : ToolbarActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: VolumesOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection_overview)

        // Bind recyclerView.
        recyclerView = this.findViewById(R.id.recyclerView)

        // Get viewModel that contains the data for the activity.
        viewModel = ViewModelProviders.of(this).get(VolumesOverviewViewModel::class.java)

        // Bind the data from viewModel to the recyclerView.
        bindData()
    }

    /**
     * Bind data to the recyclerView.
     * This makes the displayed list auto-update on changes to the database.
     */
    private fun bindData() {
        val list = viewModel.volumes
        val adapter = VolumesAdapter(list)

        // Set observer.
        list.observe(
            this, Observer { changedList -> adapter.submitList(changedList) })

        recyclerView.adapter = adapter
    }

}
