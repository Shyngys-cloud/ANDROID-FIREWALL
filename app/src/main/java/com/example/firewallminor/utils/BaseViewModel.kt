package com.example.firewallminor.utils

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

@SuppressLint("CheckResult")
abstract class BaseViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var refresh = SingleLiveData<Boolean>()
    var isRefreshing = MutableLiveData<Boolean>()
    var toastMsg = MutableLiveData<String>()
    var error = SingleLiveData<Int>()
    var errorString = SingleLiveData<String>()
    var showNetworkError = SingleLiveData<Boolean>()
    var navigateTo = SingleLiveData<Int>()
    var navigateUp = SingleLiveData<Boolean>()
    var navigateWithData = SingleLiveData<Navigation>()
    var navigateWithDataPopUp = SingleLiveData<Navigation>()
    var hideKeyboard = SingleLiveData<Boolean>()


    fun onError(t: Throwable) {
        isLoading.value = false
        isRefreshing.value = false

        t.message?.let {
            toastMsg.postValue(it)
        }
    }

    fun onSuccess() {
        isLoading.value = false
        isRefreshing.value = false
    }

}