package com.example.firewallminor.utils

import android.content.Context
import android.content.SharedPreferences

abstract class BasePrefs(context: Context) {

    var prefs: SharedPreferences? = null

    init {
        prefs = context.getSharedPreferences(
            getDefaultSharedPreferencesName(context),
            Context.MODE_PRIVATE
        )
    }

    private fun getDefaultSharedPreferencesName(context: Context)
            : String =
        context.packageName + "_preferences"


}