package de.ahahn94.manhattan.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import de.ahahn94.manhattan.R

/**
 * Activity to provide an action bar to other activities
 * via inheritance.
 */
@SuppressLint("Registered")
open class ToolbarActivity : AppCompatActivity() {

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

}