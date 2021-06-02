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
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentCreateBlogBinding
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.addCompositeDisposable
import com.fastival.jetpackwithmviapp.extension.editToString
import com.fastival.jetpackwithmviapp.extension.fragment.*
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.jakewharton.rxbinding3.widget.textChanges
import com.theartofdev.edmodo.cropper.CropImage
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
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

    var disposableBag: CompositeDisposable? = null

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

        disposableBag = CompositeDisposable()

        binding.vm = viewModel
        binding.fragment = this

        observeBlogTitle()
        observeBlogContents()
        observeImgUri()
    }

    private fun observeBlogTitle() = binding.blogTitle
        .textChanges()
        .subscribe { title ->
            binding.blogTitleLayout.apply {
                hint = if (title.isEmpty()){
                    getString(R.string.create_blog_title)
                } else {
                    if (title.length < 5)
                        "글자수: ${title.length} (5글자 이상 되어야 합니다.)"
                    else
                        "글자수: ${title.length} (제목 가능)"}
                }}
        .addCompositeDisposable(disposableBag)

    private fun observeBlogContents() = binding.blogBody
        .textChanges()
        .subscribe { contents ->
            binding.blogBodyLayout.apply {
                hint = if (contents.isEmpty())
                    getString(R.string.create_blog_body)
                else{
                    if (contents.length < 50)
                        "글자수: ${contents.length} (50글자 이상 되어야 합니다.)"
                    else
                        "글자수: ${contents.length} (본문 가능)"
                }
            }}
        .addCompositeDisposable(disposableBag)

    private fun observeImgUri() = viewModel.viewState
        .observe(viewLifecycleOwner,
            Observer { viewState ->
                viewState.blogFields.newImageUri?.let {
                    setCroppedImage(it)
                }?: deFaultImage()
            })

    override fun observeStateMessage() = viewModel.stateMessage
        .observe(viewLifecycleOwner,
            Observer { stateMessage ->
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
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri

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

    override fun onDestroyView() {
        super.onDestroyView()
        disposableBag?.clear()
        disposableBag = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Observable.combineLatest(
            binding.blogTitle.textChanges(),
            binding.blogBody.textChanges(),
            BiFunction{ title: CharSequence, contents: CharSequence ->
                title.length >= 5 && contents.length >= 50 })
            .subscribe {
                if (it){
                    if (!menu.hasVisibleItems())
                        inflater.inflate(R.menu.publish_menu, menu)
                } else {
                    menu.clear()
                }}
            .addCompositeDisposable(disposableBag)
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
