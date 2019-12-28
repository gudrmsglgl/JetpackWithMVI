package com.fastival.jetpackwithmviapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.fastival.jetpackwithmviapp.BR
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.ActivityMainBinding
import com.fastival.jetpackwithmviapp.extension.navActivity
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.auth.AuthActivity
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import com.fastival.jetpackwithmviapp.ui.main.account.ChangePasswordFragment
import com.fastival.jetpackwithmviapp.ui.main.account.UpdateAccountFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.UpdateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.ViewBlogFragment
import com.fastival.jetpackwithmviapp.util.BottomNavController
import com.fastival.jetpackwithmviapp.util.setUpNavigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding, EmptyViewModel>(),
BottomNavController.NavGraphProvider,
BottomNavController.OnNavigationGraphChanged,
BottomNavController.OnNavigationReselectedListener{

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this)
    }

    override fun getNavGraphId(itemId: Int) = when(itemId){
        R.id.nav_blog -> R.navigation.nav_blog
        R.id.nav_create_blog -> R.navigation.nav_create_blog
        R.id.nav_account -> R.navigation.nav_account
        else -> R.navigation.nav_blog
    }

    override fun onGraphChange() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onReselectNavItem(
        navController: NavController, fragment: Fragment
    ) = when(fragment){
        is ViewBlogFragment -> navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
        is UpdateBlogFragment -> navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
        is UpdateAccountFragment -> navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
        is ChangePasswordFragment -> navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
        else -> {}
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupActionBar()
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }
    }

    override fun initVariables() {
        super.initVariables()
        Log.d(TAG, "MainActivity_ sessionManager: ${sessionManager.hashCode()}")
        binding.smr = sessionManager

    }

    override fun getBindingVariable(): Int {
        return BR.emptyViewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): Class<EmptyViewModel> {
        return EmptyViewModel::class.java
    }

    override fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer {authToken->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: $authToken")


            if (authToken == null) {
                Log.d(TAG, "authToken == null")
                navActivity<AuthActivity>(true){}
            }

            authToken?.let {
                if (it.token == null || it.account_pk == -1) {
                    Log.d(TAG, "authToken.token == null || authToken.account_pk == -1")
                    navActivity<AuthActivity>(true){}
                }
            }

        })
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) progress_bar.visibility = View.VISIBLE
        else progress_bar.visibility = View.GONE
    }

    private fun setupActionBar(){
        setSupportActionBar(tool_bar)
    }
}
