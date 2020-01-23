package com.fastival.jetpackwithmviapp.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}