package com.example.firewallminor.auth

import android.content.Intent
import android.os.Bundle
import com.example.firewallminor.ActivityMain
import com.example.firewallminor.R
import com.example.firewallminor.databinding.ActivityAuthorizationBinding
import com.example.firewallminor.utils.BaseBindingActivity
import com.example.firewallminor.utils.PrefsAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity :
    BaseBindingActivity<ActivityAuthorizationBinding, PhoneViewModel>(R.layout.activity_authorization) {
    override val vm: PhoneViewModel by viewModel()
    private val prefsAuth: PrefsAuth by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        if (prefsAuth.isAuthorized()) {
            startActivity(Intent(this, ActivityMain::class.java))
            finish()
        }
    }
}