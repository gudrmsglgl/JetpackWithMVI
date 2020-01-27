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
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentCreateBlogBinding
import com.fastival.jetpackwithmviapp.extension.activity.AreYouSureCallBack
import com.fastival.jetpackwithmviapp.extension.fragment.*
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.base.create_blog.BaseCreateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel.CreateBlogViewModel
import com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel.clearNewBlogFields
import com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel.setNewBlogFields
import com.fastival.jetpackwithmviapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.theartofdev.edmodo.cropper.CropImage

/**
 * A simple [Fragment] subclass.
 */
class CreateBlogFragment
    : BaseCreateBlogFragment<FragmentCreateBlogBinding>(R.layout.fragment_create_blog)
{

    override fun getBindingVariable() = BR.vm

    override fun subscribeObservers() {

        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {

                stateListener.onDataStateChange(dataState)

                // why peekContent()?
                // baseActivity -> onDataStateChange() -> b/c Consume message (handled)
                // so message confirm -> peekContent()
                dataState.data?.response?.peekContent()?.let { response ->
                    if (response.message == SUCCESS_BLOG_CREATED) {
                        viewModel.clearNewBlogFields()
                    }
                }

            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogFields.newImageUri?.let {
                setCroppedImage(it)
            }?: deFaultImage()
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.fragment = this
    }

    fun pickFromGallery(view: View){
        if (stateListener.isStoragePermissionGranted()) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
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
            binding.blogTitle.text.toString(),
            binding.blogBody.text.toString(),
            null
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.publish_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.publish -> {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        message = getString(R.string.are_you_sure_publish),
                        uiMessageType = UIMessageType.AreYouSureDialog(object:
                            AreYouSureCallBack {
                            override fun proceed() {
                                publishNewBlog()
                            }

                            override fun cancel() {
                                // ignore
                            }
                        })
                    )
                )
            }

        }

        return super.onOptionsItemSelected(item)
    }
}
