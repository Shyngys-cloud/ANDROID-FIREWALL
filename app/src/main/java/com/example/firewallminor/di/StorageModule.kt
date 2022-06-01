package com.example.firewallminor.di

import com.example.firewallminor.utils.PrefsAuth
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    //  Storage for token, pin and other user info data
    single { PrefsAuth(androidContext()) }
}