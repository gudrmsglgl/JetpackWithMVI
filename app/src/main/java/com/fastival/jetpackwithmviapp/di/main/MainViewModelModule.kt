package com.fastival.jetpackwithmviapp.di.main

import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.di.ViewModelKey
import com.fastival.jetpackwithmviapp.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel
}