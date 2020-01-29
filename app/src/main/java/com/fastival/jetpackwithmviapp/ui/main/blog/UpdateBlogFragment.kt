package com.fastival.jetpackwithmviapp.ui.main.blog


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentUpdateBlogBinding
import com.fastival.jetpackwithmviapp.extension.fragment.launchCropImage
import com.fastival.jetpackwithmviapp.extension.fragment.saveChanges
import com.fastival.jetpackwithmviapp.extension.fragment.showErrorDialog
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.base.blog.BaseBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.fastival.jetpackwithmviapp.util.ErrorHandling
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_update_blog.*
import okhttp3.MultipartBody

/**
 * A simple [Fragment] subclass.
 */
class UpdateBlogFragment
    : BaseBlogFragment<FragmentUpdateBlogBinding>(R.layout.fragment_update_blog)
{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.requestManager = requestManager
        binding.fragment = this

        subscribeObservers()
    }

    fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {

                stateListener.onDataStateChange(dataState)
                dataState.data?.data?.getContentIfNotHandled()?.let { viewState ->

                    viewState.viewBlogFields.blogPost?.let { blogPost ->

                        viewModel.setSyncBlogFromServer(blogPost).run {
                            findNavController().popBackStack()
                        }

                    }
                }

            }
        })

    }

    fun pickFromGallery(view: View){
        if (stateListener.isStoragePermissionGranted()) {

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply{
                type = "image/*"
                val mimeTypes = arrayOf("image/jpeg","image/png","image/jpg")
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {

                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        launchCropImage(uri)
                    }?:showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri

                    viewModel.setUpdatedBlogFields(
                        title = null,
                        body = null,
                        uri = resultUri
                    )
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
            null
        )
    }
    override fun getVariableId(): Int = BR.vm
}
