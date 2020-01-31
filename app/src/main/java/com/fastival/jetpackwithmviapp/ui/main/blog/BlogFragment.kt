package com.fastival.jetpackwithmviapp.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentBlogBinding
import com.fastival.jetpackwithmviapp.extension.fragment.*
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.base.blog.BaseBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling
import kotlinx.android.synthetic.main.fragment_blog.*


class BlogFragment : BaseBlogFragment<FragmentBlogBinding>(R.layout.fragment_blog),
 BlogListAdapter.Interaction,
 SwipeRefreshLayout.OnRefreshListener
{

    internal lateinit var recyclerAdapter: BlogListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeObservers()
        viewModel.loadFirstPage()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)

        Log.d(TAG, "blogFragment_ViewModel: $viewModel")
        initRecyclerView()

        // restore view
        if (viewModel.getCurrentViewStateOrNew().blogFields.blogList.isNotEmpty()) {
            viewModel.refreshFromCache()
        }
    }

     fun subscribeObservers() {
         viewModel.dataState.observe(this, Observer {dataState ->
             if (dataState != null) {
                 handlePagination(dataState)
                 stateListener.onDataStateChange(dataState)
             }
         })

         viewModel.viewState.observe(this, Observer { viewState ->
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
        viewModel.setBlogPost(item, true)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun restoreListPosition() {
        val viewState = viewModel.getCurrentViewStateOrNew()
        viewState.blogFields.layoutManagerState?.let { layoutManagerState ->
            blog_post_recyclerview?.layoutManager?.onRestoreInstanceState(layoutManagerState)
        }
    }

    override fun onPause() {
        super.onPause()
        saveLayoutManagerState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        blog_post_recyclerview.adapter = null
    }

    override fun onRefresh() {
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing = false
    }

    override fun getVariableId(): Int = BR.vm

}
