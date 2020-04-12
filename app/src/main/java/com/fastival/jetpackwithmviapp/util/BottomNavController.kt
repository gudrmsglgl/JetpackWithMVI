package com.fastival.jetpackwithmviapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.fragments.main.account.AccountNavHostFragment
import com.fastival.jetpackwithmviapp.fragments.main.blog.BlogNavHostFragment
import com.fastival.jetpackwithmviapp.fragments.main.create_blog.CreateBlogNavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

const val BOTTOM_NAV_BACKSTACK_KEY =
    "om.fastival.jetpackwithmviapp.util.BottomNavController.bottom_nav_backstack"

@ExperimentalCoroutinesApi
@FlowPreview
class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val startBottomMenuId: Int,
    val graphChangeListener: OnNavigationGraphChanged?
) {

    private val TAG: String = "AppDebug"

    lateinit var navigationBackStack: BackStack

    lateinit var activity: Activity

    lateinit var fragmentManager: FragmentManager

    lateinit var navItemChangeListener: OnNavigationItemChanged

    init {
        if (context is Activity) {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }


    fun setupBottomNavigationBackStack(previousBackStack: BackStack?){
        navigationBackStack = previousBackStack?.let {
                it
        }?: BackStack.of(startBottomMenuId)
    }


    fun onBottomNavigationItemSelected(
        menuItemId: Int = navigationBackStack.last()
    ): Boolean {

        // Replace fragment representing a navigation item
        val navHost =
            fragmentManager.findFragmentByTag(menuItemId.toString()) ?: createNavHost(menuItemId)

        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(
                containerId,
                navHost,
                menuItemId.toString()
            )
            .addToBackStack(null)
            .commit()

        // Add to back stack
        navigationBackStack.addLast(menuItemId)

        // update checked icon
        navItemChangeListener.onItemChanged(menuItemId)

        // communicate with Activity
        graphChangeListener?.onGraphChange()

        return true
    }


    private fun createNavHost(menuItemId: Int): Fragment =
        when(menuItemId) {

            R.id.bottom_menu_blog -> {
                BlogNavHostFragment.create(R.navigation.nav_blog)
            }

            R.id.bottom_menu_create_blog -> {
                CreateBlogNavHostFragment.create(R.navigation.nav_create_blog)
            }

            R.id.bottom_menu_account -> {
                AccountNavHostFragment.create(R.navigation.nav_account)
            }

            else -> BlogNavHostFragment.create(R.navigation.nav_blog)
        }


    @SuppressLint("RestrictedApi")
    fun onBackPressed(){

        val navController = fragmentManager.findFragmentById(containerId)!!
            .findNavController()


        when{
            // We should always try to go back on the child fragment manager stack before going to
            // the navigation stack. It's important to use the child fragment manager instead of the
            // NavController because if the user change tabs super fast commit of the
            // supportFragmentManager may mess up with the NavController child fragment manager back
            // stack

            // default backStack size 3 on Dept 1
            navController.backStack.size > 2 ->{
                navController.popBackStack()
            }

            // Fragment back stack is empty so try to go back on the navigation stack
            navigationBackStack.size > 1 -> {
                // Remove last item from back stack
                navigationBackStack.removeLast()

                // Update the container with new fragment
                onBottomNavigationItemSelected()
            }

            // If the stack has only one and it's not the navigation home we should
            // ensure that the application always leave from startDestination
            navigationBackStack.last() != startBottomMenuId -> {
                navigationBackStack.removeLast()
                navigationBackStack.add(0, startBottomMenuId)
                onBottomNavigationItemSelected()
            }
            // Navigation staci is empty, so finish the activity
            else -> activity.finish()
        }
    }


    @Parcelize
    class BackStack: ArrayList<Int>(), Parcelable {

        companion object {

            fun of(vararg elements: Int) =
                BackStack()
                    .apply { addAll(elements.toTypedArray())}

        }

        fun removeLast() = removeAt(size -1)

        fun addLast(item: Int) {
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
@ExperimentalCoroutinesApi
@FlowPreview
// Convenience extension to set up the navigation
fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onReselectListener: BottomNavController.OnNavigationReselectedListener
){


    this.setOnNavigationItemSelectedListener { menuItem->
        bottomNavController.onBottomNavigationItemSelected(menuItem.itemId)
    }

    this.setOnNavigationItemReselectedListener {

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

        bottomNavController.onBottomNavigationItemSelected()
    }

    bottomNavController.setOnItemNavigationChanged { itemId ->
        menu.findItem(itemId).isChecked = true
    }

}