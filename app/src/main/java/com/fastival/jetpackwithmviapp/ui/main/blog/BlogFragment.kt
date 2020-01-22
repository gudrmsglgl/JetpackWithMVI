package com.fastival.jetpackwithmviapp.ui.main.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentBlogBinding
import com.fastival.jetpackwithmviapp.extension.initRecyclerView
import com.fastival.jetpackwithmviapp.extension.initSearchView
import com.fastival.jetpackwithmviapp.extension.onBlogSearchOrFilter
import com.fastival.jetpackwithmviapp.extension.showFilterDialog
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling
import com.fastival.jetpackwithmviapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*


class BlogFragment : BaseMainFragment<FragmentBlogBinding, BlogViewModel>(),
 BlogListAdapter.Interaction,
 SwipeRefreshLayout.OnRefreshListener
{

    internal lateinit var recyclerAdapter: BlogListAdapter

    override fun setTopLevelDesId(): Int = R.id.blogFragment

    override fun getBindingVariable(): Int = BR.vm

    override fun getLayoutId(): Int = R.layout.fragment_blog

    override fun getViewModel(): Class<BlogViewModel> = BlogViewModel::class.java

    override fun initFunc() {
        initRecyclerView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)

        Log.d(TAG, "blogFragment_ViewModel: $viewModel")

        if (savedInstanceState == null) {
            viewModel.loadFirstPage()
        }
    }

    override fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState ->
            if (dataState != null) {
                handlePagination(dataState)
                stateListener.onDataStateChange(dataState)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            Log.d(TAG, "BlogFragment, ViewState: $viewState")
            if (viewState != null ) {
                recyclerAdapter.apply {

                    preloadGlideImages(
                        requestManager = requestManager,
                        list = viewState.blogFields.blogList)

                    submitList(
                        viewState.blogFields.blogList,
                        viewState.blogFields.isQueryExhausted)
                }
            }

        })
    }

    private fun handlePagination(dataState: DataState<BlogViewState>) {

        // Handle incoming data from DataState
        dataState.data?.data?.getContentIfNotHandled()?.let { viewState ->
            viewModel.handleIncomingBlogListData(viewState)
        }

        // Check for pagination end (no more results)
        // must do this b/c server will return an ApiErrorResponse if page is not valid,
        // -> meaning there is no more data.
        dataState.error?.let { event ->
            event.peekContent().response.message?.let{
                Log.e(TAG, "dataState.error-> pagination: $it")
                if (ErrorHandling.isPaginationDone(it)) {

                    // handle the error message event so it doesn't display in UI
                    event.getContentIfNotHandled()

                    // set query exhausted to update RecyclerView with
                    // "No more results... " list item
                    viewModel.setQueryExhausted(true)

                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
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

    override fun onItemSelected(position: Int, item: BlogPost) {
        Log.d(TAG, "onItemSelected: position, BlogPost: $position, $item")
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        blog_post_recyclerview.adapter = null
    }

    override fun onRefresh() {
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing = false
    }

}
