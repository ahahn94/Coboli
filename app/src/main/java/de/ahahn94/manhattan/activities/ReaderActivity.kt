package de.ahahn94.manhattan.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.adapters.PagesAdapter
import de.ahahn94.manhattan.model.views.CachedIssuesView
import de.ahahn94.manhattan.viewModels.ReaderViewModel

/**
 * Handles the reader view that displays the pages of a comic.
 */
class ReaderActivity : AppCompatActivity() {

    private lateinit var pagesContainer: ViewPager

    companion object {

        // Tag of the issue that is handed over to the activity via Bundle.
        const val ISSUE = "issue"

    }

    /**
     * OnCreate-function.
     * Customizations:
     * - load layout
     * - get the issue from Bundle/Extras
     * - bind viewModel
     * - bind container for the pages images
     * - add the pages list to the container via adapter.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        // Get issue.
        val issue = intent.getSerializableExtra(ISSUE) as CachedIssuesView

        val viewModel = ViewModelProviders.of(
            this,
            ReaderViewModel.Factory(issue)
        ).get(ReaderViewModel::class.java)

        pagesContainer = findViewById(R.id.PagesContainer)

        val pages = viewModel.pages

        val adapter = PagesAdapter(this, pages.value ?: arrayListOf())

        viewModel.pages.observe(this, Observer {
            adapter.submitList(it)
        })

        pagesContainer.adapter = adapter

        pagesContainer.offscreenPageLimit = 1
    }

}