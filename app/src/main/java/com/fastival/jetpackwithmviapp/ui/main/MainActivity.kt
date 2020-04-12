package com.fastival.jetpackwithmviapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.fastival.jetpackwithmviapp.BaseApplication
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.extension.activity.navActivity
import com.fastival.jetpackwithmviapp.extension.activity.restoreSession
import com.fastival.jetpackwithmviapp.extension.activity.setupBottomNavigationView
import com.fastival.jetpackwithmviapp.extension.fragment.setPositionTopRecyclerView
import com.fastival.jetpackwithmviapp.models.AUTH_TOKEN_BUNDLE_KEY
import com.fastival.jetpackwithmviapp.ui.BaseActivity
import com.fastival.jetpackwithmviapp.ui.auth.AuthActivity
import com.fastival.jetpackwithmviapp.ui.main.account.ChangePasswordFragment
import com.fastival.jetpackwithmviapp.ui.main.account.UpdateAccountFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.UpdateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.ViewBlogFragment
import com.fastival.jetpackwithmviapp.util.BOTTOM_NAV_BACKSTACK_KEY
import com.fastival.jetpackwithmviapp.util.BottomNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Named

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : BaseActivity(),
BottomNavController.OnNavigationGraphChanged,
BottomNavController.OnNavigationReselectedListener
{

    internal lateinit var bottomNavigationView: BottomNavigationView

    @Inject
    @Named("AccountFragmentFactory")
    lateinit var accountFragmentFactory: FragmentFactory

    @Inject
    @Named("BlogFragmentFactory")
    lateinit var blogFragmentFactory: FragmentFactory

    @Inject
    @Named("CreateBlogFragmentFactory")
    lateinit var createBlogFragmentFactory: FragmentFactory

    internal val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_fragments_container,
            R.id.bottom_menu_blog,
            this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tool_bar)

        restoreSession(savedInstanceState)

        setupBottomNavigationView(savedInstanceState)

        observeCachedToken()
    }


    private fun observeCachedToken() =
        sessionManager.cachedToken.observe(this, Observer { authToken->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: $authToken")

            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {

                navActivity<AuthActivity>(true){}.run {
                    (application as BaseApplication).releaseMainComponent()
                }

            }
        })


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState){

            putParcelable(AUTH_TOKEN_BUNDLE_KEY, sessionManager.cachedToken.value)

            putIntArray(BOTTOM_NAV_BACKSTACK_KEY, bottomNavController.navigationBackStack.toIntArray())

        }
    }


    override fun onReselectNavItem(
        navController: NavController,
        fragment: Fragment
    ) = when(fragment){
        is BlogFragment -> fragment.setPositionTopRecyclerView()
        is ViewBlogFragment -> navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
        is UpdateBlogFragment -> navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
        is UpdateAccountFragment -> navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
        is ChangePasswordFragment -> navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
        else -> {}
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun displayProgressBar(isAnyActiveJob: Boolean) {
        if (isAnyActiveJob) progress_bar.visibility = View.VISIBLE
        else progress_bar.visibility = View.GONE
    }


    override fun onGraphChange() {
        expandAppBar()
    }


    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }


    override fun onBackPressed() =
        bottomNavController.onBackPressed()


    override fun inject() =
        (application as BaseApplication).mainComponent().inject(this)

}
