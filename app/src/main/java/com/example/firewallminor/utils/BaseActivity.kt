package com.example.firewallminor.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity(), IResourcesIDListener {

    companion object {
        const val TAG = "BaseActivity"
    }

    private var dialogForKMFLoader: Dialog? = null
    private var isShowMsg = false

    //  Initialize all widget in layout
    protected abstract fun initViews(savedInstanceState: Bundle?)
    protected abstract fun onCreateUI(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateUI(savedInstanceState)
    }


    /*  Modal windows  */
    open fun toast(context: Context?, msg: Any, isDuration: Boolean = false) {
        context?.let {
            val duration = if (isDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            when (msg) {
                is String ->
                    Toast.makeText(context, msg, duration).show()
                is Int ->
                    Toast.makeText(context, msg, duration).show()
            }
        }
    }
    /*  Resources ID getters  */

    /*
     *  If your app supported more one language,
     *  you can add your locale
     *  example -> yourResources.getString(id);
     */
    override fun getStr(@StringRes id: Int): String = getString(id)

    /*
     * Concat all your text, strings and resources,
     * to one String
     */
    override fun concatStr(text: String): String = text

    /*
     * Get drawable (png, jpg, svg, ....) by ID
     */
    @Nullable
    override fun getImg(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

    /*
     * Get color by ID
     */
    @ColorInt
    override fun getClr(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)
}