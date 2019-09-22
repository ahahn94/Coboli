package de.ahahn94.manhattan.activities

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import de.ahahn94.manhattan.R
import de.ahahn94.manhattan.fragments.IssuesFragment
import de.ahahn94.manhattan.fragments.PublishersFragment
import de.ahahn94.manhattan.fragments.VolumesFragment
import de.ahahn94.manhattan.utils.ContextProvider

/**
 * Activity to provide an action bar and navigation drawer
 * to other activities via inheritance.
 */
@SuppressLint("Registered")
open class ToolbarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FragmentedActivity {

    private lateinit var drawer: DrawerLayout
    private lateinit var container: FrameLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Save application context into ContextProvider.
        ContextProvider.setApplicationContext(applicationContext)

        // Load layout.
        setContentView(R.layout.activity_toolbar)

        // Bind container for activity content.
        container = findViewById(R.id.container)

        // Use toolbar as ActionBar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // Set navigation drawer.
        drawer = findViewById(R.id.drawerLayout)
        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Show default fragment.
        showDefaultFragment()
    }

    /**
     * onNewIntent-function.
     * Customizations:
     * - if the intent action is ACTION_SEARCH, run the searchVolumes function
     * to show the VolumesFragment with only the volumes that match the search query.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent?.action == Intent.ACTION_SEARCH) {
            searchVolumes(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    /**
     * Close the drawer if it is open when pressing the back button.
     * Else go back to previous view.
     */
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Drawer is open. Close drawer.
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Fragment BackStack is not empty. Navigate back to the previous fragment.
                supportFragmentManager.popBackStackImmediate()

                // Set the navigationView checked item to match the displayed collection.
                val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
                when (currentFragment?.javaClass) {
                    VolumesFragment::class.java -> {
                        if (currentFragment.arguments?.getBoolean(
                                VolumesFragment.CACHED_VOLUMES,
                                false
                            ) == true
                        ) {
                            // Downloaded volumes.
                            navigationView.setCheckedItem(R.id.navigationDownloaded)
                        } else {
                            // All volumes.
                            navigationView.setCheckedItem(R.id.navigationVolumes)
                        }
                    }
                    PublishersFragment::class.java -> {
                        navigationView.setCheckedItem(R.id.navigationPublishers)
                    }
                }
            } else {
                super.onBackPressed()
            }
        }
    }

    /**
     * Inflate the main menu into the action bar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)

        // bind searchable config to SearchView.
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.ActionSearch)?.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return true
    }

    /**
     * Trigger actions on clicks on menu items.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.ActionSync -> {
                startActivity(Intent(this, SyncActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Trigger actions on clicks on navigation drawer items.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigationVolumes -> {
                val fragment = VolumesFragment()
                replaceFragmentBackStack(fragment)

            }
            R.id.navigationPublishers -> {
                val fragment = PublishersFragment()
                replaceFragmentBackStack(fragment)
            }
            R.id.navigationDownloaded -> {
                val fragment = VolumesFragment()
                val bundle = Bundle()
                bundle.putBoolean(VolumesFragment.CACHED_VOLUMES, true)
                fragment.arguments = bundle
                replaceFragmentBackStack(fragment)
            }
            R.id.navigationReadingList -> {
                val fragment = IssuesFragment()
                val bundle = Bundle()
                bundle.putBoolean(IssuesFragment.READING_LIST, true)
                fragment.arguments = bundle
                replaceFragmentBackStack(fragment)
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Search for volumes by name and show a list of the results.
     */
    private fun searchVolumes(query: String) {
        val fragment = VolumesFragment()
        val bundle = Bundle()
        bundle.putString(VolumesFragment.SEARCH_QUERY, query)
        fragment.arguments = bundle
        replaceFragmentBackStack(fragment)
        navigationView.setCheckedItem(R.id.navigationVolumes)
    }

    /**
     * Show the default fragment inside the FrameLayout container.
     * VolumesFragment is default.
     */
    private fun showDefaultFragment() {
        replaceFragment(VolumesFragment())
        navigationView.setCheckedItem(R.id.navigationVolumes)
    }

    /**
     * Replace the fragment inside the FrameLayout container with another.
     */
    override fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    /**
     * Replace the fragment inside the FrameLayout container with another.
     * Register the fragment to the fragment BackStack to enable going back
     * to the previous fragment.
     */
    override fun replaceFragmentBackStack(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}