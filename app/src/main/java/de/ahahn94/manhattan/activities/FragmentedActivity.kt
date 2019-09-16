package de.ahahn94.manhattan.activities

import androidx.fragment.app.Fragment

/**
 * Interface for activities that use a container for fragments to provide their content.
 */
interface FragmentedActivity {

    /**
     * Replace the fragment inside the container with another.
     */
    fun replaceFragment(fragment: Fragment)

    /**
     * Replace the fragment inside the container with another.
     * Register the fragment to the fragment BackStack to enable going back
     * to the previous fragment.
     */
    fun replaceFragmentBackStack(fragment: Fragment)

}