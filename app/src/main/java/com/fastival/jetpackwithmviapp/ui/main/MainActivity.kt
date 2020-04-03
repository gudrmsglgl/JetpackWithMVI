package com.fastival.jetpackwithmviapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.fastival.jetpackwithmviapp.BR
import com.fastival.jetpackwithmviapp.BaseApplication
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.ActivityMainBinding
import com.fastival.jetpackwithmviapp.extension.activity.navActivity
import com.fastival.jetpackwithmviapp.extension.fragment.setPositionTopRecyclerView
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.auth.AuthActivity
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.base.account.BaseAccountFragment
import com.fastival.jetpackwithmviapp.ui.base.blog.BaseBlogFragment
import com.fastival.jetpackwithmviapp.ui.base.create_blog.BaseCreateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.account.ChangePasswordFragment
import com.fastival.jetpackwithmviapp.ui.main.account.UpdateAccountFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.UpdateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.ViewBlogFragment
import com.fastival.jetpackwithmviapp.util.BottomNavController
import com.fastival.jetpackwithmviapp.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Named

class MainActivity : BaseActivity(),
BottomNavController.OnNavigationGraphChanged,
BottomNavController.OnNavigationReselectedListener{

    private lateinit var bottomNavigationView: BottomNavigationView

    @Inject
    @Named("AccountFragmentFactory")
    lateinit var accountFragmentFactory: FragmentFactory

    @Inject
    @Named("BlogFragmentFactory")
    lateinit var blogFragmentFactory: FragmentFactory

    @Inject
    @Named("CreateBlogFragmentFactory")
    lateinit var createBlogFragmentFactory: FragmentFactory


    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this)
    }


    override fun onGraphChange() {
        cancelActiveJobs()
        expandAppBar()
    }

    private fun cancelActiveJobs() {
        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments

        Log.d(TAG, "MainActivity_in_fragments: ${fragments?.size}")

        if (fragments != null) {
            for (fragment in fragments) {
                when(fragment) {
                    is BaseAccountFragment<*> -> {
                        Log.d(TAG, "MainActivity_cancelActiveJobs()_AccountSection")
                        fragment.cancelActiveJobs()
                    }
                    is BaseBlogFragment<*> -> {
                        Log.d(TAG, "MainActivity_cancelActiveJobs()_BlogSection")
                        fragment.cancelActiveJobs()
                    }
                    is BaseCreateBlogFragment<*> -> {
                        Log.d(TAG, "MainActivity_cancelActiveJobs()_CreateBlogSection")
                        fragment.cancelActiveJobs()
                    }
                }
            }
        }

        displayProgressBar(false)
    }

    override fun onReselectNavItem(
        navController: NavController, fragment: Fragment
    ) = when(fragment){
        is BlogFragment -> fragment.setPositionTopRecyclerView()
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


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer {authToken->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: $authToken")


            if (authToken == null) {
                Log.d(TAG, "authToken == null")
                navActivity<AuthActivity>(true){}.run {
                    (application as BaseApplication).releaseMainComponent()
                }
            }

            authToken?.let {
                if (it.token == null || it.account_pk == -1) {
                    Log.d(TAG, "authToken.token == null || authToken.account_pk == -1")
                    navActivity<AuthActivity>(true){}.run {
                        (application as BaseApplication).releaseMainComponent()
                    }
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

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    override fun inject() {
        (application as BaseApplication).mainComponent().inject(this)
    }
}
