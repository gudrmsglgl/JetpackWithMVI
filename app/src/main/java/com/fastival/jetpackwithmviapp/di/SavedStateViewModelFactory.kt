package com.fastival.jetpackwithmviapp.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

interface SavedStateViewModelFactory<T: ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}