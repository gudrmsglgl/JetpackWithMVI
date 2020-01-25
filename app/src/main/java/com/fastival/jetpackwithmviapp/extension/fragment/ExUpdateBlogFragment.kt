package com.fastival.jetpackwithmviapp.extension.fragment

import android.net.Uri
import com.fastival.jetpackwithmviapp.UBF
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun UBF.launchCropImage(uri: Uri) = context?.let {
    CropImage.activity(uri)
        .setGuidelines(CropImageView.Guidelines.ON)
        .start(it, this)
}

fun UBF.showErrorDialog(message: String){
    val dataState = DataState(
        error = Event(StateError(Response(message, ResponseType.Dialog()))),
        data = Data(data = Event(null), response = null),
        loading = Loading(false)
    )
    stateListener.onDataStateChange(dataState)
}

fun UBF.saveChanges(){
    // 1) uri -> RequestBody -> MultiPartBody.Part
    // 2) event -> updateBlog

    var multipartBody: MultipartBody.Part? = null
    val viewState = viewModel.getCurrentViewStateOrNew()
    viewState.updatedBlogFields.updatedImageUri?.let { uri ->
        uri.path?.let { path ->

            val file = File(path)
            val requestBody = RequestBody.create(
                MediaType.parse("image/*"),
                file
            )

            multipartBody = MultipartBody.Part.createFormData(
                "image",
                file.name,
                requestBody
            )
        }
    }

    viewModel.setStateEvent(
        BlogStateEvent.UpdateBlogPostEvent(
            title = binding.blogTitle.text.toString(),
            body = binding.blogBody.text.toString(),
            image = multipartBody
        )
    )

    stateListener.hideSoftKeyboard()

}