package com.fastival.jetpackwithmviapp.extension.activity

import android.os.Bundle
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.models.AUTH_TOKEN_BUNDLE_KEY
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.ui.main.MainActivity
import com.fastival.jetpackwithmviapp.util.BOTTOM_NAV_BACKSTACK_KEY
import com.fastival.jetpackwithmviapp.util.BottomNavController
import com.fastival.jetpackwithmviapp.util.setUpNavigation

fun MainActivity.setupBottomNavigationView(savedInstanceState: Bundle?) {

    bottomNavigationView = findViewById(R.id.bottom_navigation_view)
    bottomNavigationView.setUpNavigation(bottomNavController, this)

    with(bottomNavController) {
        savedInstanceState?.let {
            restoreBottomNavBackStack(this, it)
        }?: initBottomNavBackStack(this)
    }

}

private fun restoreBottomNavBackStack(
    bottomNavController: BottomNavController,
    bundle: Bundle
){
    (bundle[BOTTOM_NAV_BACKSTACK_KEY] as IntArray)?.let { storedIds ->
        val backStack = BottomNavController.BackStack()
        backStack.addAll(storedIds.toTypedArray())
        bottomNavController.setupBottomNavigationBackStack(backStack)
    }
}


private fun initBottomNavBackStack(bottomNavController: BottomNavController) =
    with(bottomNavController) {
        setupBottomNavigationBackStack(null)
        onNavigationItemSelected()
    }


fun MainActivity.restoreSession(savedInstanceState: Bundle?){
    savedInstanceState?.get(AUTH_TOKEN_BUNDLE_KEY)?.let { authToken ->
        sessionManager.setValue(authToken as AuthToken)
    }
}