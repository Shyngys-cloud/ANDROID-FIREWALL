package com.example.firewallminor.utils

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.firewallminor.R

abstract class BaseBindingActivity<B : ViewDataBinding, T : BaseViewModel>(@LayoutRes private val layoutResID: Int) :
    BaseActivity() {

    companion object {
//        const val TAG = "Base BindingActivity"
    }


    private var dialogForLoader: Dialog? = null

    private var isNetworkWasLost = false
    private var isShowMsg = false

    protected abstract val vm: BaseViewModel

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    //  Your view data binding
    lateinit var binding: B

    override fun onCreateUI(savedInstanceState: Bundle?) {
        //  Override Resources ID Layout
        binding = DataBindingUtil.setContentView(this, layoutResID)
        //  Initialize all widget in layout by ID

        vm.isLoading.observe(this) {
            if (it) {
                showLoader()
            } else {
                hideLoader()
            }
        }
        vm.error.observe(this) {
            showMsg(getStr(it))
        }
        vm.errorString.observe(this) {
            showMsg(it)
        }
        vm.navigateWithData.observe(this) {
            startActivity(Intent(this, it.destinationActivity).apply {
                putExtra("NAME", it.bundle)
            })
        }
        vm.navigateUp.observe(this) {
            onBackPressed()
        }
        vm.hideKeyboard.observe(this) {
            hideKeyboard()
        }
        registerNetworkCallback()
        initViews(savedInstanceState)
    }


    private fun hideKeyboard() {
        window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }


    private fun showLoader() {
        this.let {
            killDialog()
            dialogForLoader = Dialog(this)
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

    private fun killDialog() {
        if (dialogForLoader != null)
            dialogForLoader = null
    }


    private fun hideLoader() {
        dialogForLoader?.dismiss()
        killDialog()
    }


    private fun registerNetworkCallback() {
        val networkCallback: ConnectivityManager.NetworkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if (isNetworkWasLost)
                        vm.refresh.postValue(true)
                }

                override fun onLost(network: Network) {
                    isNetworkWasLost = true
                    vm.isRefreshing.postValue(false)
                }
            }

        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (!isNetworkAvailable(connectivityManager))
            isNetworkWasLost = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
    }

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }


    private fun showMsg(msg: String) {
        if (isShowMsg) return
        Dialog(this).apply {
            this.setContentView(R.layout.layout_msg)
            this.setCancelable(false)
            this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            this.setCanceledOnTouchOutside(false)
            this.findViewById<Button>(R.id.btnClose).setOnClickListener {
                isShowMsg = false
                dismiss()
            }
            this.findViewById<TextView>(R.id.dialogContent).text = msg
            this.show()
            isShowMsg = true
        }
    }

    private fun showMsg(msg: String, callback: () -> Unit) {
        if (isShowMsg) return
        Dialog(this).apply {
            this.setContentView(R.layout.layout_msg)
            this.setCancelable(false)
            this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            this.setCanceledOnTouchOutside(false)
            this.findViewById<Button>(R.id.btnClose).setOnClickListener {
                isShowMsg = false
                dismiss()
                callback.invoke()
            }
            this.findViewById<TextView>(R.id.dialogContent).text = msg
            this.show()
            isShowMsg = true
        }
    }

}