package com.fastival.jetpackwithmviapp.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentBlogBinding
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.addCompositeDisposable
import com.fastival.jetpackwithmviapp.extension.fragment.*
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
@MainScope
class BlogFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory,
    var requestOptions: RequestOptions
): BaseBlogFragment<FragmentBlogBinding>(R.layout.fragment_blog, provider), SwipeRefreshLayout.OnRefreshListener
{

    internal lateinit var recyclerAdapter: BlogListAdapter
    internal var requestManager: RequestManager? = null // can leak memory, must be nullable
    var disposableBag: CompositeDisposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposableBag = CompositeDisposable()
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        swipe_refresh.setOnRefreshListener(this)
        setupGlide()
        initRecyclerView()
        observeBlogList()
        observeBlogClick()
        observeBlogListState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFromCache()
    }

    private fun observeBlogList() = viewModel.viewState
        .observe(viewLifecycleOwner,
            Observer { viewState ->
                viewState.blogFields.blogList?.let {

                    recyclerAdapter.apply {
                        preloadGlideImages(
                            requestManager = requestManager as RequestManager,
                            list = it
                        )
                        submitList(
                            blogList = it,
                            isQueryExhausted = viewModel.getIsQueryExhausted()
                        )
                    }

                }
            })

    private fun observeBlogClick() = recyclerAdapter.clickSubject
        .doOnNext { Log.d(TAG, "blogClick_blogInfo: $it") }
        .subscribe {
            navBlogView(it)
        }
        .addCompositeDisposable(disposableBag)

    private fun observeBlogListState() = recyclerAdapter.restoreListPosSubject
        .subscribe{ restoreListPosition() }
        .addCompositeDisposable(disposableBag)

    override fun observeStateMessage() = viewModel.stateMessage
        .observe(viewLifecycleOwner,
            Observer { stateMessage ->
                stateMessage?.let {
                    if (isPaginationDone(it.response.message)){
                        paginationDone(viewModel)
                    }
                    else {
                        showStateMessage(it, viewModel)
                    }

                }
            })

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ){
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navBlogView(blogPost: BlogPost) = viewModel.setBlogPost(blogPost)
        .run{ findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment) }

    private fun restoreListPosition() = with(viewModel){
        getCurrentViewStateOrNew()
            .blogFields
            .layoutManagerState
            ?.let {
                blog_post_recyclerview
                    ?.layoutManager
                    ?.onRestoreInstanceState(it)
            }
    }

    override fun onPause() {
        super.onPause()
        saveLayoutManagerState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposableBag?.clear()
        disposableBag = null
        blog_post_recyclerview.adapter = null
        requestManager = null
    }

    override fun onRefresh() {
        fetchBlog()
        swipe_refresh.isRefreshing = false
    }
}
