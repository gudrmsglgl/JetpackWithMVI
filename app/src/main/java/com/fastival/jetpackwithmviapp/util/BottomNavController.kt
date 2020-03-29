package com.fastival.jetpackwithmviapp.util

import android.app.Activity
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.fragments.main.account.AccountNavHostFragment
import com.fastival.jetpackwithmviapp.fragments.main.blog.BlogNavHostFragment
import com.fastival.jetpackwithmviapp.fragments.main.create_blog.CreateBlogNavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val appStartDestinationId: Int,
    val graphChangeListener: OnNavigationGraphChanged?
) {
    private val TAG: String = "AppDebug"
    private val navigationBackStack = BackStack.of(appStartDestinationId)
    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    lateinit var navItemChangeListener: OnNavigationItemChanged


    init {
        if (context is Activity) {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun onNavigationItemSelected(menuItemId: Int = navigationBackStack.last()): Boolean {

        // Replace fragment representing a navigation item
        val fragment = fragmentManager.findFragmentByTag(menuItemId.toString())
            ?: createNavHost(menuItemId)
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, menuItemId.toString())
            .addToBackStack(null)
            .commit()

        // Add to back stack
        navigationBackStack.moveLast(menuItemId)

        // update checked icon
        navItemChangeListener.onItemChanged(menuItemId)

        // communicate with Activity
        graphChangeListener?.onGraphChange()

        return true
    }

    private fun createNavHost(menuItemId: Int): Fragment =
        when(menuItemId) {

            R.id.nav_blog -> {
                BlogNavHostFragment.create(R.navigation.nav_blog)
            }

            R.id.nav_create_blog -> {
                CreateBlogNavHostFragment.create(R.navigation.nav_create_blog)
            }

            R.id.nav_account -> {
                AccountNavHostFragment.create(R.navigation.nav_account)
            }

            else -> BlogNavHostFragment.create(R.navigation.nav_blog)
        }

    fun onBackPressed(){
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
            .childFragmentManager
        when{
            // We should always try to go back on the child fragment manager stack before going to
            // the navigation stack. It's important to use the child fragment manager instead of the
            // NavController because if the user change tabs super fast commit of the
            // supportFragmentManager may mess up with the NavController child fragment manager back
            // stack

            childFragmentManager.popBackStackImmediate() -> {
            }

            // Fragment back stack is empty so try to go back on the navigation stack
            navigationBackStack.size > 1 -> {
                // Remove last item from back stack
                navigationBackStack.removeLast()

                // Update the container with new fragment
                onNavigationItemSelected()
            }

            // If the stack has only one and it's not the navigation home we should
            // ensure that the application always leave from startDestination
            navigationBackStack.last() != appStartDestinationId -> {
                navigationBackStack.removeLast()
                navigationBackStack.add(0, appStartDestinationId)
                onNavigationItemSelected()
            }
            // Navigation staci is empty, so finish the activity
            else -> activity.finish()
        }
    }

    private class BackStack: ArrayList<Int>(){

        companion object {
            fun of(vararg elements: Int): BackStack {
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }

        fun removeLast() = removeAt(size -1)

        fun moveLast(item: Int) {
            remove(item) // if present, remove
            add(item)
        }
    }


    // Checked icon in the bottom nav
    interface OnNavigationItemChanged{
        fun onItemChanged(itemId: Int)
    }



    // Execute when Navigation Graph changes.
    // ex: Select a new item on the bottom nav
    // ex: Home -> Account
    interface OnNavigationGraphChanged{
        fun onGraphChange()
    }


    interface OnNavigationReselectedListener{
        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }

    fun setOnItemNavigationChanged(listener: (itemId: Int) -> Unit) {
        this.navItemChangeListener = object : OnNavigationItemChanged {
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }
        }
    }


}

// Convenience extension to set up the navigation
fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onReselectListener: BottomNavController.OnNavigationReselectedListener
){
    setOnNavigationItemSelectedListener {menuItem->
        bottomNavController.onNavigationItemSelected(menuItem.itemId)
    }

    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager
            .findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let { fragment ->

            onReselectListener.onReselectNavItem(
                bottomNavController.activity.findNavController(bottomNavController.containerId),
                fragment
            )
        }
    }

    bottomNavController.setOnItemNavigationChanged { itemId ->
        menu.findItem(itemId).isChecked = true
    }
}