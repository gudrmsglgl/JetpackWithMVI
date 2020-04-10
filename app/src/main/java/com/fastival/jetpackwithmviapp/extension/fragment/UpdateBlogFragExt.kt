package com.fastival.jetpackwithmviapp.extension.fragment

import android.content.Intent
import android.net.Uri
import com.fastival.jetpackwithmviapp.extension.editToString
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.ui.main.blog.UpdateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.util.MessageType
import com.fastival.jetpackwithmviapp.util.Response
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import com.fastival.jetpackwithmviapp.util.UIComponentType
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import id.zelory.compressor.Compressor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
fun UpdateBlogFragment.setViewStateUri(data: Intent?){

    val result = CropImage.getActivityResult(data)
    val resultUri = result.uri

    with (viewModel){
        getCurrentViewStateOrNew()
            .apply {
                updatedBlogFields.updatedImageUri = resultUri }
            .run {
                setViewState(this)
            }
    }

}


@ExperimentalCoroutinesApi
@FlowPreview
fun UpdateBlogFragment.launchCropImage(uri: Uri) = context?.let {
    CropImage.activity(uri)
        .setGuidelines(CropImageView.Guidelines.ON)
        .setMaxCropResultSize(1280*2, 960*2)
        .start(it, this)
}


@ExperimentalCoroutinesApi
@FlowPreview
fun UpdateBlogFragment.showErrorDialog(message: String){
    uiCommunicationListener.onResponseReceived(
        response = Response(
            message = message,
            uiComponentType = UIComponentType.Dialog,
            messageType = MessageType.Error
        ),
        stateMessageCallback = object : StateMessageCallback{
            override fun removeMessageFromStack() {
                viewModel.removeStateMessage()
            }
        }
    )
}


@ExperimentalCoroutinesApi
@FlowPreview
fun UpdateBlogFragment.saveChanges(){
    // 1) uri -> RequestBody -> MultiPartBody.Part
    // 2) event -> updateBlog
    context?.let { context ->

        var multipartBody: MultipartBody.Part? = null
        val viewState = viewModel.getCurrentViewStateOrNew()
        viewState.updatedBlogFields.updatedImageUri?.let { uri ->
            uri.path?.let { path ->

                val file = File(path)
                if (file.exists()) {
                    val compressedImageFile: File
                            = Compressor(context)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .compressToFile(file)

                    val requestBody = RequestBody.create(
                        MediaType.parse("image/*"),
                        compressedImageFile
                    )

                    multipartBody = MultipartBody.Part.createFormData(
                        "image",
                        compressedImageFile.name,
                        requestBody
                    )
                }
            }
        }

        viewModel.setStateEvent(
            BlogStateEvent.UpdateBlogPostEvent(
                title = binding.blogTitle.editToString(),
                body = binding.blogBody.editToString(),
                image = multipartBody
            )
        )

        uiCommunicationListener.hideSoftKeyboard()

    }
}