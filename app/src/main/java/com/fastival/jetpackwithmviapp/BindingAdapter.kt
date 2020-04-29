package com.fastival.jetpackwithmviapp

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

object BindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["url", "requestManager","uri"] , requireAll = false)
    fun bindingImage(view: ImageView, url: String?, requestManager: RequestManager, uri: Uri?) {
        if (url != null && uri == null) {
            requestManager
                .load(url)
        } else {
            requestManager
                .load(uri)
        }
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }

}