package com.example.firewallminor.di

import com.example.firewallminor.auth.CodeViewModel
import com.example.firewallminor.auth.PhoneViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CodeViewModel(get(), get()) }
    viewModel { PhoneViewModel(get()) }
}