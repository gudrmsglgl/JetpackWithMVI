package com.fastival.jetpackwithmviapp.ui.main.create_blog


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentCreateBlogBinding
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

/**
 * A simple [Fragment] subclass.
 */
class CreateBlogFragment : BaseMainFragment<FragmentCreateBlogBinding, CreateBlogViewModel>() {

    override fun setTopLevelDesId(): Int = R.id.createBlogFragment

    override fun getBindingVariable() = BR.vm

    override fun initFunc() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_create_blog

    override fun getViewModel(): Class<CreateBlogViewModel> = CreateBlogViewModel::class.java

    override fun subscribeObservers() {

        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateListener.onDataStateChange(dataState)
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogFields.newImageUri?.let {
                setCroppedImage(it)
            }?:deFaultImage()
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun launchImageCrop(uri: Uri) {
        context?.let {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        stateListener.onDataStateChange(
           DataState(
               error = Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
               loading = Loading(false),
               data = Data(Event.dataEvent(null), null)
           )
        )
    }
    private fun setCroppedImage(image: Uri){
        requestManager
            .load(image)
            .into(binding.blogImage)
    }

    private fun deFaultImage(){
        requestManager
            .load(R.drawable.default_image)
            .into(binding.blogImage)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFields(
            binding.blogTitle.text.toString(),
            binding.blogBody.text.toString(),
            null
        )
    }
}
