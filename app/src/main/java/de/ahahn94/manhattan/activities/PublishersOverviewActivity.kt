package de.ahahn94.manhattan.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.adapters.PublishersAdapter
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.viewModels.PublishersOverviewViewModel

/**
 * Class to handle the publishers overview activity.
 */
class PublishersOverviewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: PublishersOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection_overview)

        // Bind recyclerView.
        recyclerView = this.findViewById(R.id.recyclerView)

        // Get viewModel that contains the data for the activity.
        viewModel = ViewModelProviders.of(this).get(PublishersOverviewViewModel::class.java)

        // Bind the data from viewModel to the recyclerView.
        bindData()
    }

    /**
     * Bind data to the recyclerView.
     * This makes the displayed list auto-update on changes to the database.
     */
    private fun bindData() {
        val list = viewModel.publishers
        val adapter = PublishersAdapter(list)

        // Set observer.
        list.observe(
            this,
            Observer { changedList -> adapter.submitList(changedList) })

        this.recyclerView.adapter = adapter
    }

}
