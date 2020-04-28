package com.fastival.jetpackwithmviapp.extension

import android.view.View
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

fun Disposable.addCompositeDisposable(
    disposableBag: CompositeDisposable?
) = disposableBag?.add(this)

fun View.singleClick()=
    clicks().throttleFirst(2000, TimeUnit.MILLISECONDS)

