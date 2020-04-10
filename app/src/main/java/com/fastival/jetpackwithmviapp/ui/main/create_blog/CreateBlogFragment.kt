package com.fastival.jetpackwithmviapp.ui.main.create_blog


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentCreateBlogBinding
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.editToString
import com.fastival.jetpackwithmviapp.extension.fragment.*
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class CreateBlogFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory,
    val requestManager: RequestManager
): BaseCreateBlogFragment<FragmentCreateBlogBinding>(R.layout.fragment_create_blog, provider)
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CREATE_BLOG_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.fragment = this

        observeImgUri()
    }


    fun observeImgUri() =
        viewModel
            .viewState
            .observe(viewLifecycleOwner, Observer { viewState ->
                viewState.blogFields.newImageUri?.let {
                    setCroppedImage(it)
                }?: deFaultImage()
            })


    override fun observeStateMessage() =
        viewModel
            .stateMessage
            .observe( viewLifecycleOwner, Observer { stateMessage ->

                stateMessage?.let {

                    if (it.response.message == SUCCESS_BLOG_CREATED)
                        viewModel.clearNewBlogFields()

                    uiCommunicationListener.onResponseReceived(
                        response = it.response,
                        stateMessageCallback = object : StateMessageCallback {

                            override fun removeMessageFromStack() {

                                viewModel.removeStateMessage()

                            }
                        }
                    )

                }
            })


    fun pickFromGallery(view: View){
        if (uiCommunicationListener.isStoragePermissionGranted()) {

            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                    val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
                    putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

            startActivityForResult(intent, GALLERY_REQUEST_CODE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "CROP: RESULT OK")

            when(requestCode) {

                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let {
                            launchImageCrop(uri)
                        }
                    }?: showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")

                    val result = CropImage.getActivityResult(data)

                    val resultUri = result.uri

                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri: $resultUri")

                    viewModel.setNewBlogFields(
                        title = null,
                        body = null,
                        uri = resultUri
                    )

                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.d(TAG, "CROP: ERROR")
                    showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

            }
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFields(
            binding.blogTitle.editToString(),
            binding.blogBody.editToString(),
            null
        )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.publish_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.publish -> {
                showDialogConfirmPublish()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }


}
