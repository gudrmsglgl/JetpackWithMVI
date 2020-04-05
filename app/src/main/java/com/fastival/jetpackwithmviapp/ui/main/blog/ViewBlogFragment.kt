package com.fastival.jetpackwithmviapp.ui.main.blog


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentViewBlogBinding
import com.fastival.jetpackwithmviapp.extension.activity.AreYouSureCallBack
import com.fastival.jetpackwithmviapp.ui.UIMessage
import com.fastival.jetpackwithmviapp.ui.UIMessageType
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ViewBlogFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseBlogFragment<FragmentViewBlogBinding>(R.layout.fragment_view_blog, provider)
{

    fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner , Observer { dateState ->
            if (dateState != null){

                stateListener.onDataStateChange(dateState)
                dateState.data?.data?.getContentIfNotHandled()?.let { viewState ->
                    viewModel.setIsAuthorOfBlogPost(
                        viewState.viewBlogFields.isAuthorOfBlogPost
                    )
                }

                dateState.data?.response?.peekContent()?.let { response ->
                    if (response.message == SUCCESS_BLOG_DELETED) {
                        viewModel.removeDeletedBlogPost()
                        findNavController().popBackStack()
                    }
                }

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
        binding.fragment = this

        subscribeObservers()
        checkIsAuthor()
    }

    private fun checkIsAuthor() {
        viewModel.setIsAuthorOfBlogPost(false) // reset
        viewModel.setStateEvent(BlogStateEvent.CheckAuthorOfBlogPost())
    }

    fun confirmDeleteRequest(view: View) {
        uiCommunicationListener.onUIMessageReceived(UIMessage(
            message = getString(R.string.are_you_sure_delete),
            uiMessageType = UIMessageType.AreYouSureDialog(callback = object:
                AreYouSureCallBack {
                override fun proceed() {
                    viewModel.setStateEvent(
                        BlogStateEvent.DeleteBlogPostEvent()
                    )
                }

                override fun cancel() {
                    // ignore
                }
            })
        ))
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
        try{
            viewModel.setUpdatedBlogFields(
                title = viewModel.getBlogPost().title,
                body = viewModel.getBlogPost().body,
                uri = viewModel.getBlogPost().image.toUri(),
                isViewStateUpdate = true
            )
            findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
        } catch (e: Exception){
            // send error report or something. These fields should never be null. Not possible
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

    override fun getVariableId(): Int = BR.vm

}
