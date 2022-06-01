package com.example.firewallminor.utils

import android.app.Application
import com.example.firewallminor.di.appModule
import com.example.firewallminor.di.storageModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import repoModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, repoModule, storageModule))
        }
    }
}
