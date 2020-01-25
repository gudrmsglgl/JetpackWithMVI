package com.fastival.jetpackwithmviapp.extension.fragment

import android.net.Uri
import android.util.Log
import com.fastival.jetpackwithmviapp.CBF
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.fastival.jetpackwithmviapp.util.ErrorHandling
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import id.zelory.compressor.Compressor


fun CBF.launchImageCrop(uri: Uri) {
    context?.let {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMaxCropResultSize(1280*2, 960*2)
            .start(it, this)
    }
}

fun CBF.showErrorDialog(errorMessage: String) {
    stateListener.onDataStateChange(
        DataState(
            error = Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
            loading = Loading(false),
            data = Data(Event.dataEvent(null), null)
        )
    )
}

fun CBF.setCroppedImage(image: Uri){
    requestManager
        .load(image)
        .into(binding.blogImage)
}

fun CBF.deFaultImage(){
    requestManager
        .load(R.drawable.default_image)
        .into(binding.blogImage)
}

fun CBF.publishNewBlog(){
    context?.let { context ->

        var multipartBody: MultipartBody.Part? = null

        viewModel.viewState.value?.blogFields?.newImageUri?.let { imgUri ->
            imgUri.path?.let { path ->

                val imageFile = File(path)
                Log.d(TAG, "CreateBlogFragment, imageFile: file: $imageFile , size: ${imageFile.length()}")

                val compressedImageFile: File
                        = Compressor(context)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .compressToFile(imageFile)

                Log.d(TAG, "CreateBlogFragment, compressedImageFile: $compressedImageFile , size: ${compressedImageFile.length()}")
                val requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    compressedImageFile
                )

                multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    compressedImageFile.name,
                    requestBody
                ) // name, filename, requestBody

            }
        }

        multipartBody?.let {

            viewModel.setStateEvent(
                CreateBlogStateEvent.CreateNewBlogEvent(
                    binding.blogTitle.text.toString(),
                    binding.blogBody.text.toString(),
                    it
                )
            )
            stateListener.hideSoftKeyboard()

        }?: showErrorDialog(ErrorHandling.ERROR_MUST_SELECT_IMAGE)
    }
}