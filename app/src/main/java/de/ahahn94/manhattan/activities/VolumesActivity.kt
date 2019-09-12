package de.ahahn94.manhattan.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.adapters.VolumesAdapter
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.viewModels.VolumesViewModel
import java.lang.ref.WeakReference

/**
 * Class to handle the volumes overview activity.
 */
class VolumesActivity : ToolbarActivity() {

    companion object {

        const val PUBLISHER_ID_NAME = "publisherID"

    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: VolumesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection)

        // Get publisherID.
        val publisherID = intent.getStringExtra(PUBLISHER_ID_NAME) ?: ""

        // Bind recyclerView.
        recyclerView = this.findViewById(R.id.recyclerView)

        // Get viewModel that contains the data for the activity.
        viewModel = ViewModelProviders.of(
            this,
            VolumesViewModel.Factory(this.application, publisherID)
        ).get(VolumesViewModel::class.java)

        // Bind the data from viewModel to the recyclerView.
        bindData()
    }

    /**
     * Bind data to the recyclerView.
     * This makes the displayed list auto-update on changes to the database.
     */
    private fun bindData() {
        // Bind data to recyclerView.
        val list = viewModel.volumes
        val adapter = VolumesAdapter(WeakReference(supportFragmentManager), list)

        // Set observer.
        list.observe(
            this, Observer { changedList -> adapter.submitList(changedList) })

        recyclerView.adapter = adapter
    }

}