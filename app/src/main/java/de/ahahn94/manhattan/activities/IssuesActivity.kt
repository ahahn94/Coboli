package de.ahahn94.manhattan.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.adapters.IssuesAdapter
import de.ahahn94.manhattan.utils.ContextProvider
import de.ahahn94.manhattan.viewModels.IssuesViewModel
import java.lang.ref.WeakReference

/**
 * Class to handle the issues activity.
 */
class IssuesActivity : ToolbarActivity() {

    companion object {

        // Extra-IDs for values passed into the activity at creation.
        const val VOLUME_ID_NAME = "volumeID"
        const val CACHED_ISSUES = "cachedIssues"

    }

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: IssuesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load activity layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection)

        // Get volumeID.
        val volumeID = intent.getStringExtra(VOLUME_ID_NAME)

        // Only cached issues?
        val cachedOnly = intent.getBooleanExtra(CACHED_ISSUES, false)

        // Bind recyclerView.
        recyclerView = this.findViewById(R.id.recyclerView)

        // Get viewModel that contains the data for the activity.
        viewModel = ViewModelProviders.of(
            this,
            IssuesViewModel.Factory(this.application, volumeID, cachedOnly)
        ).get(IssuesViewModel::class.java)

        // Bind the data from viewModel to the recyclerView.
        bindData()
    }

    /**
     * Bind data to the recyclerView and the cardView.
     * This makes the displayed data auto-update on changes to the database.
     */
    private fun bindData() {

        // Bind data to recyclerView.
        val list = viewModel.issues
        val adapter = IssuesAdapter(WeakReference(supportFragmentManager), list)

        // Set observer.
        list.observe(
            this,
            Observer { changedList ->
                adapter.submitList(changedList)
            })

        this.recyclerView.adapter = adapter
    }

}
