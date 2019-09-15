package de.ahahn94.manhattan.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import de.ahahn94.manhattan.R

/**
 * Activity to provide an action bar to other activities
 * via inheritance.
 */
@SuppressLint("Registered")
open class ToolbarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawer: DrawerLayout
    lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load layout.
        setContentView(R.layout.activity_toolbar)

        // Bind container for activity content.
        container = findViewById(R.id.container)

        // Use toolbar as ActionBar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
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

    }

    /**
     * Close the drawer if it is open when pressing the back button.
     * Else go back to previous view.
     */
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Inflate the main menu into the action bar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
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
                startActivity(Intent(this, VolumesActivity::class.java))
            }
            R.id.navigationPublishers -> {
                startActivity(Intent(this, PublishersActivity::class.java))
            }
            R.id.navigationDownloaded -> {
                val intent = Intent(this, VolumesActivity::class.java)
                intent.putExtra(VolumesActivity.CACHED_VOLUMES, true)
                startActivity(intent)
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}