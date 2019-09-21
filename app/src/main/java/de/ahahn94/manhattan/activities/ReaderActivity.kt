package de.ahahn94.manhattan.activities

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.adapters.PagesAdapter
import de.ahahn94.manhattan.model.views.CachedIssuesView
import de.ahahn94.manhattan.utils.Timestamps
import de.ahahn94.manhattan.viewModels.ReaderViewModel

/**
 * Handles the reader view that displays the pages of a comic.
 */
class ReaderActivity : AppCompatActivity() {

    private lateinit var loadingContainer: LinearLayout
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

        loadingContainer = findViewById(R.id.LoadingComicContainer)
        pagesContainer = findViewById(R.id.PagesContainer)

        val pages = viewModel.pages

        val adapter = PagesAdapter(this, pages.value ?: arrayListOf())

        viewModel.pages.observe(this, Observer {
            if (pages.error) finish()   // Close activity if error during caching of comic.
            adapter.submitList(it)
            if (it.size > 0) {
                // List is not empty, loading is done.
                // Hide loading screen and show comic pages.
                pagesContainer.visibility = View.VISIBLE
                loadingContainer.visibility = View.GONE
            }

            // Jump to current page from last reading session as soon as it is loaded.
            val currentPage = issue.readStatus.currentPage.toInt()
            if (it.size == (currentPage + 1)) pagesContainer.currentItem = currentPage
        })

        pagesContainer.adapter = adapter

        pagesContainer.offscreenPageLimit = 1

        // Listen on page changes.
        pagesContainer.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            /**
             * Mandatory function.
             * Not in use.
             */
            override fun onPageScrollStateChanged(state: Int) {
            }

            /**
             * Mandatory function.
             * Not in use.
             */
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            /**
             * Update the CurrentPage number and the IsRead status of the issue on the database.
             */
            override fun onPageSelected(position: Int) {
                // If reaching the last page, set IsRead to true. Else false.
                issue.readStatus.isRead = if (position == (adapter.count) - 1) "1" else "0"
                AsyncTask.execute {
                    de.ahahn94.manhattan.model.Database.getInstance().issuesDao()
                        .updateReadStatus(
                            issue.id,
                            issue.readStatus.isRead,
                            position.toString(),
                            Timestamps.nowToUtcTimestamp()
                        )
                }
            }

        })
    }

}