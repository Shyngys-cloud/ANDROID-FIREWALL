package com.example.firewallminor.utils

import android.os.Bundle

data class Navigation(
    val destination: Int,
    val destinationActivity: Class<*>?,
    val bundle: Bundle,
)
