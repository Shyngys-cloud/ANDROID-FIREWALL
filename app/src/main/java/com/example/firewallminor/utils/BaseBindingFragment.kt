package com.example.firewallminor.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.example.firewallminor.R

abstract class BaseBindingFragment<B : ViewDataBinding, T : BaseViewModel>(@LayoutRes private val layoutResID: Int) :
    BaseFragment() {

    protected abstract val vm: BaseViewModel

    private var isShowMsg = false

    //  Your view data binding
    lateinit var binding: B

    private var isNetworkWasLost = false

    //  Bind all widgets and start code
    protected abstract fun initViews(savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResID, container, false)
        return binding.root
    }


    // Initialize all widget in layout by ID
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoader()
            } else {
                hideLoader()
            }
        }
        vm.error.observe(viewLifecycleOwner) {
            showMsg(getStr(it))
        }
        vm.errorString.observe(viewLifecycleOwner) {
            showMsg(it)
        }
        vm.showNetworkError.observe(viewLifecycleOwner) {
        }
        vm.navigateTo.observe(viewLifecycleOwner) {
            findNavController().navigate(it)
        }
        vm.navigateWithData.observe(viewLifecycleOwner) {
            findNavController().navigate(it.destination, it.bundle)
        }
        vm.navigateUp.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        vm.hideKeyboard.observe(viewLifecycleOwner) {
            hideKeyboard()
        }
        registerNetworkCallback()
        initViews(savedInstanceState)
    }

    private fun hideKeyboard() {
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
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
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    fun showMsg(msg: String) {
        if (isShowMsg) return
        Dialog(requireContext()).apply {
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

    fun showMsg(msg: String, callback: () -> Unit) {
        if (isShowMsg) return
        Dialog(requireContext()).apply {
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