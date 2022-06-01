package com.example.firewallminor.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.firewallminor.R


abstract class BaseFragment : Fragment(), IResourcesIDListener {

    companion object {
        const val TAG = "BaseFragment"
    }

    private var dialogForLoader: Dialog? = null

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

    fun showLoader() {
        activity?.let {
            killDialog()
            dialogForLoader = Dialog(requireActivity())
            dialogForLoader?.let { dialog ->
                dialog.setContentView(R.layout.custom_dialog)
                dialog.setCancelable(false)
                dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
            }
        }
    }

    fun hideLoader() {
        dialogForLoader?.dismiss()
        killDialog()
    }

    private fun killDialog() {
        if (dialogForLoader != null)
            dialogForLoader = null
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
    override fun getImg(@DrawableRes id: Int): Drawable? =
        ContextCompat.getDrawable(requireActivity(), id)

    /*
     * Get color by ID
     */
    @ColorInt
    override fun getClr(@ColorRes id: Int): Int = ContextCompat.getColor(requireActivity(), id)
}