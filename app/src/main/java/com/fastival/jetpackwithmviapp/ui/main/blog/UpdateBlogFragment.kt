package com.fastival.jetpackwithmviapp.ui.main.blog


import android.os.Bundle
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentUpdateBlogBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.getBlogPost
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.setSyncBlogsFromServer
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.setUpdatedBlogFields
import kotlinx.android.synthetic.main.fragment_update_blog.*
import okhttp3.MultipartBody

/**
 * A simple [Fragment] subclass.
 */
class UpdateBlogFragment : BaseMainFragment<FragmentUpdateBlogBinding, BlogViewModel>() {

    override fun setTopLevelDesId(): Int = R.id.blogFragment

    override fun getBindingVariable(): Int = BR.vm

    override fun initFunc() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_update_blog

    override fun getViewModel(): Class<BlogViewModel> = BlogViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.requestManager = requestManager
    }

    override fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateListener.onDataStateChange(dataState)
            dataState?.data?.data?.getContentIfNotHandled()?.let { viewState ->
                viewState.viewBlogFields.blogPost?.let { blogPost ->

                    viewModel.setSyncBlogsFromServer(blogPost).run {
                        findNavController().popBackStack()
                    }

                }
            }
        })

    }

    private fun saveChanges(){
        var multipartBody: MultipartBody.Part? = null
        viewModel.setStateEvent(
            BlogStateEvent.UpdateBlogPostEvent(
                blog_title.text.toString(),
                blog_body.text.toString(),
                multipartBody
            )
        )
        stateListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUpdatedBlogFields(
            blog_title.text.toString(),
            blog_body.text.toString(),
            viewModel.getBlogPost().image.toUri()
        )
    }
}
