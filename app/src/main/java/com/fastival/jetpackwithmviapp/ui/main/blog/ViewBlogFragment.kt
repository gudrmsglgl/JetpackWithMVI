package com.fastival.jetpackwithmviapp.ui.main.blog


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentViewBlogBinding
import com.fastival.jetpackwithmviapp.extension.convertLongToStringDate
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.isAuthorOfBlogPost
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.setIsAuthorOfBlogPost
import kotlinx.android.synthetic.main.fragment_view_blog.*

/**
 * A simple [Fragment] subclass.
 */
class ViewBlogFragment : BaseMainFragment<FragmentViewBlogBinding, BlogViewModel>() {

    override fun setTopLevelDesId(): Int = R.id.blogFragment

    override fun getBindingVariable(): Int = BR.vm

    override fun initFunc() {
        checkIsAuthor()
    }

    override fun getLayoutId(): Int = R.layout.fragment_view_blog

    override fun getViewModel(): Class<BlogViewModel> = BlogViewModel::class.java

    override fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner , Observer { dateState ->
            stateListener.onDataStateChange(dateState)

            dateState.data?.data?.getContentIfNotHandled()?.let { viewState ->
                viewModel.setIsAuthorOfBlogPost(
                    viewState.viewBlogFields.isAuthorOfBlogPost
                )
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            if (viewState.viewBlogFields.isAuthorOfBlogPost) {
                activity?.invalidateOptionsMenu() // isAuthor -> menu inflate
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        stateListener.expandAppBar()
        binding.requestManager = requestManager
        binding.deleteEvent = BlogStateEvent.DeleteBlogPostEvent()
    }

    private fun checkIsAuthor() {
        viewModel.setIsAuthorOfBlogPost(false) // reset
        viewModel.setStateEvent(BlogStateEvent.CheckAuthorOfBlogPost())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       if (viewModel.isAuthorOfBlogPost()) inflater.inflate(R.menu.edit_view_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfBlogPost()) {
            when(item.itemId) {
                R.id.edit -> {
                    navUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateBlogFragment(){
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}
