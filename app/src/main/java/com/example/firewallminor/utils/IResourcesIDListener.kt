package com.example.firewallminor.utils

import android.graphics.drawable.Drawable
import androidx.annotation.*

/*  Resources ID getters  */
interface IResourcesIDListener {
    fun getStr(@StringRes id: Int): String
    fun concatStr(text: String): String
    @Nullable
    fun getImg(@DrawableRes id: Int): Drawable?
    @ColorInt
    fun getClr(@ColorRes id: Int): Int
}