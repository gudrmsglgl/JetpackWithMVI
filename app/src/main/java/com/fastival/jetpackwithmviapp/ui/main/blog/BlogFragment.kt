package com.fastival.jetpackwithmviapp.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentBlogBinding
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
 BlogListAdapter.Interaction{

    private lateinit var recyclerAdapter: BlogListAdapter

    override fun setTopLevelDesId(): Int = R.id.blogFragment

    override fun getBindingVariable(): Int = BR.vm

    override fun initFunc() {
        initRecyclerView()
    }

    private fun initRecyclerView() {

        blog_post_recyclerview.apply {

            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = BlogListAdapter(requestManager, this@BlogFragment)
            addOnScrollListener(object : RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.nextPage()
                    }
                }
            })

            adapter = recyclerAdapter
        }

    }


    override fun getLayoutId(): Int = R.layout.fragment_blog

    override fun getViewModel(): Class<BlogViewModel> = BlogViewModel::class.java

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
                recyclerAdapter.submitList(
                    viewState.blogFields.blogList,
                    viewState.blogFields.isQueryExhausted)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        Log.d(TAG, "blogFragment_ViewModel: $viewModel")

        if (savedInstanceState == null) {
            viewModel.loadFirstPage()
        }
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

    override fun onItemSelected(position: Int, item: BlogPost) {
        Log.d(TAG, "onItemSelected: position, BlogPost: $position, $item")
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        blog_post_recyclerview.adapter = null
    }
}
