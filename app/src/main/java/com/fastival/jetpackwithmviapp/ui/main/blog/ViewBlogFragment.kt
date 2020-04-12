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
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentViewBlogBinding
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.activity.AreYouSureCallBack
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.MessageType
import com.fastival.jetpackwithmviapp.util.Response
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import com.fastival.jetpackwithmviapp.util.UIComponentType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@FlowPreview
@ExperimentalCoroutinesApi
@MainScope
class ViewBlogFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory,
    private val injectedReqManager: RequestManager
): BaseBlogFragment<FragmentViewBlogBinding>(R.layout.fragment_view_blog, provider)
{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()

        checkIsAuthor()

        observeIsAuthor()
    }

    override fun observeStateMessage() =
        viewModel
            .stateMessage
            .observe(viewLifecycleOwner, Observer { stateMessage ->

                stateMessage?.let {

                    if (it.response.message == SUCCESS_BLOG_DELETED)
                        viewModel.removeDeletedBlogPost().run { findNavController().popBackStack() }

                    uiCommunicationListener
                        .onResponseReceived(
                            response = it.response,
                            stateMessageCallback = object: StateMessageCallback{
                                override fun removeMessageFromStack() {
                                    viewModel.removeStateMessage()
                                }
                            }
                        )

                }

            })

    private fun observeIsAuthor() = viewModel.viewState
        .observe( viewLifecycleOwner, Observer { viewState ->

            if (viewState.viewBlogFields.isAuthorOfBlogPost == true)
                invalidateOptionMenu()

        })


    private fun invalidateOptionMenu() = activity?.invalidateOptionsMenu()

    private fun checkIsAuthor() = with(viewModel){
        setIsAuthorOfBlogPost(false) // reset
        setStateEvent(
            BlogStateEvent.CheckAuthorOfBlogPost()
        )
    }


    private fun initDataBinding() = binding.apply {
        vm = viewModel
        requestManager = injectedReqManager
        fragment = this@ViewBlogFragment
    }


    fun confirmDeleteRequest(view: View) {

        val callBack: AreYouSureCallBack = object: AreYouSureCallBack{
            override fun proceed() {

                viewModel.setStateEvent(
                    BlogStateEvent.DeleteBlogPostEvent()
                )

            }

            override fun cancel() {
                // ignore
            }
        }

        uiCommunicationListener.onResponseReceived(
            response = Response(
                message = getString(R.string.are_you_sure_delete),
                uiComponentType = UIComponentType.AreYouSureDialog(callBack),
                messageType = MessageType.Info
            ),
            stateMessageCallback = object: StateMessageCallback{
                override fun removeMessageFromStack() {
                    viewModel.removeStateMessage()
                }
            }
        )

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

            with(viewModel) {
                setUpdatedBlogFields(
                    title = getBlogPost().title,
                    body = getBlogPost().body,
                    uri = getBlogPost().image.toUri())
            }

            findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)

        } catch (e: Exception){
            // send error report or something. These fields should never be null. Not possible
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

}
