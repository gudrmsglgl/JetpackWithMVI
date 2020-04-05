package com.fastival.jetpackwithmviapp.extension.fragment

import android.net.Uri
import android.util.Log
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.extension.activity.AreYouSureCallBack
import com.fastival.jetpackwithmviapp.ui.main.create_blog.CreateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.Response
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import id.zelory.compressor.Compressor


fun CreateBlogFragment.launchImageCrop(uri: Uri) {
    context?.let {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMaxCropResultSize(1280*2, 960*2)
            .start(it, this)
    }
}

fun CreateBlogFragment.showErrorDialog(errorMessage: String) =
    uiCommunicationListener.onResponseReceived(
        response = Response(
            message = errorMessage,
            uiComponentType = UIComponentType.Dialog,
            messageType = MessageType.Error
        ),
        stateMessageCallback = object: StateMessageCallback{
            override fun removeMessageFromStack() {

                // TODO :: 이거 맞나 확인해봐야함 b/c repository 에서 addStateMessage() 가 안됬기 때문
                viewModel.removeStateMessage()

            }
        }
    )


fun CreateBlogFragment.setCroppedImage(image: Uri){
    requestManager
        .load(image)
        .into(binding.blogImage)
}

fun CreateBlogFragment.deFaultImage(){
    requestManager
        .load(R.drawable.default_image)
        .into(binding.blogImage)
}

fun CreateBlogFragment.showDialogConfirmPublish(){

    val callback: AreYouSureCallBack = object: AreYouSureCallBack{
        override fun proceed() {
            publishNewBlog()
        }

        override fun cancel() {}
    }

    uiCommunicationListener.onResponseReceived(
        response = Response(
            message = getString(R.string.are_you_sure_publish),
            uiComponentType = UIComponentType.AreYouSureDialog(callback),
            messageType = MessageType.Info
        ),
        stateMessageCallback = object : StateMessageCallback{
            override fun removeMessageFromStack() {
                viewModel.removeStateMessage()
            }
        }
    )
}



fun CreateBlogFragment.publishNewBlog(){
    context?.let { context ->

        var multipartBody: MultipartBody.Part? = null

        viewModel.viewState.value?.blogFields?.newImageUri?.let { imgUri ->
            imgUri.path?.let { path ->

                multipartBody = transMultiPartBodyPart(path)

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
            uiCommunicationListener.hideSoftKeyboard()

        }?: showErrorDialog(ErrorHandling.ERROR_MUST_SELECT_IMAGE)
    }
}

private fun CreateBlogFragment.transMultiPartBodyPart(
    filePath: String
): MultipartBody.Part {
    val imageFile = File(filePath)

    val compressedImageFile: File =
        Compressor(context)
            .setMaxWidth(640)
            .setMaxHeight(480)
            .setQuality(75)
            .compressToFile(imageFile)

    Log.d(TAG, "CreateBlogFragment, compressedImageFile: $compressedImageFile , size: ${compressedImageFile.length()}")

    val requestBody = RequestBody.create(
        MediaType.parse("image/*"),
        compressedImageFile
    )

    return MultipartBody.Part.createFormData(
        "image",
        compressedImageFile.name,
        requestBody
    ) // name, filename, requestBody
}