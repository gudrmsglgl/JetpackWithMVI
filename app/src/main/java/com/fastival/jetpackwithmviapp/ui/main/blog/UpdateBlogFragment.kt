package com.fastival.jetpackwithmviapp.ui.main.blog


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentUpdateBlogBinding
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.editToString
import com.fastival.jetpackwithmviapp.extension.fragment.launchCropImage
import com.fastival.jetpackwithmviapp.extension.fragment.saveChanges
import com.fastival.jetpackwithmviapp.extension.fragment.setViewStateUri
import com.fastival.jetpackwithmviapp.extension.fragment.showErrorDialog
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_UPDATED
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_update_blog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@FlowPreview
@ExperimentalCoroutinesApi
@MainScope
class UpdateBlogFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : BaseBlogFragment<FragmentUpdateBlogBinding>(R.layout.fragment_update_blog, provider)
{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.requestManager = requestManager
        binding.fragment = this

    }

    override fun observeStateMessage() =
        viewModel
            .stateMessage
            .observe( viewLifecycleOwner, Observer { stateMessage ->

                stateMessage?.let {

                    if (it.response.message == SUCCESS_BLOG_UPDATED)
                        viewModel
                            .updateBlogListItem()
                            .run {
                                findNavController().popBackStack()
                            }

                    uiCommunicationListener
                        .onResponseReceived(
                            response = it.response,
                            stateMessageCallback = object: StateMessageCallback {
                                override fun removeMessageFromStack() {
                                    viewModel.removeStateMessage()
                                }
                            }
                        )

                }

            })

    fun pickFromGallery(view: View){
        if (uiCommunicationListener.isStoragePermissionGranted()) {

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
                    setViewStateUri(data)
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
            blog_title.editToString(),
            blog_body.editToString(),
            null
        )
    }


}
