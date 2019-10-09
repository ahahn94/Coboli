package de.ahahn94.coboli.activities

import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import de.ahahn94.coboli.R
import de.ahahn94.coboli.adapters.PagesAdapter
import de.ahahn94.coboli.fragments.PagesOverviewFragment
import de.ahahn94.coboli.menus.ReaderPopupMenu
import de.ahahn94.coboli.model.Database
import de.ahahn94.coboli.model.views.CachedIssuesView
import de.ahahn94.coboli.utils.Timestamps
import de.ahahn94.coboli.viewModels.ReaderViewModel
import java.lang.ref.WeakReference

/**
 * Handles the reader view that displays the pages of a comic.
 */
class ReaderActivity : AppCompatActivity() {

    private lateinit var loadingContainer: LinearLayout
    private lateinit var pagesContainer: ViewPager
    private lateinit var menuFrame: FrameLayout
    private lateinit var menuButton: Button

    companion object {

        // Tag of the issue that is handed over to the activity via Bundle.
        const val ISSUE = "issue"

    }

    private lateinit var viewModel: ReaderViewModel

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

        viewModel = ViewModelProviders.of(
            this,
            ReaderViewModel.Factory(issue)
        ).get(ReaderViewModel::class.java)

        // Bind layout elements.
        loadingContainer = findViewById(R.id.container_loading_comic)
        pagesContainer = findViewById(R.id.pager_pages)
        menuFrame = findViewById(R.id.frame_menu)
        menuButton = findViewById(R.id.button_menu)

        val pages = viewModel.pages

        val adapter = PagesAdapter(this, menuFrame, pages.value ?: arrayListOf())

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
            val currentPage = issue.readStatus.currentPage
            // currentPage - 1 because the ComicLib reader counts from 1 while the
            // ViewPager counts from 0.
            if (it.size == (currentPage)) pagesContainer.currentItem = currentPage - 1
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
                issue.readStatus.isRead =
                    position == (adapter.count) - 1 &&
                    // The "resume previous reading session"-function is triggered as soon as the
                    // required page is loaded, which makes it the (temp.) last page and triggers
                    // the "mark as read" part. The following line prevents this.
                    position != issue.readStatus.currentPage.toInt()

                AsyncTask.execute {
                    Database.getInstance().issuesDao()
                        .updateReadStatus(
                            issue.id,
                            issue.readStatus.isRead,
                            // + 1 because the Reader from ComicLib counts from 1,
                            // while ViewPager counts from 0.
                            position + 1,
                            Timestamps.nowToUtcTimestamp()
                        )
                }
            }

        })

        // Add popup menu to the menu button.
        menuButton.setOnClickListener {
            val menu = ReaderPopupMenu(this, menuButton, Gravity.END)
            menu.setOnMenuItemClickListener {
                when (it.itemId) {

                    R.id.action_jump_to_cover -> {
                        // Jump to the cover page.
                        pagesContainer.currentItem = 0
                        menuFrame.visibility = View.INVISIBLE
                        true
                    }

                    R.id.action_pages_overview -> {
                        // Show the pages overview.
                        val transaction = supportFragmentManager.beginTransaction()
                        val dialog = PagesOverviewFragment(
                            viewModel.thumbnails,
                            pagesContainer.currentItem,
                            WeakReference(pagesContainer)
                        )
                        dialog.show(transaction, "Pages Overview")
                        menuFrame.visibility = View.INVISIBLE
                        true
                    }

                    else -> {
                        false
                    }

                }
            }
            menu.show()
        }
    }

    /**
     * Destroy activity.
     * Triggered by finish().
     */
    override fun onDestroy() {
        // Stop loading thumbnails if task is still running.
        viewModel.thumbnailsLoader.cancel(true)
        super.onDestroy()
    }

}