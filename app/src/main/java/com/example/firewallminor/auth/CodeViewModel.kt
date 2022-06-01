package com.example.firewallminor.auth

import android.annotation.SuppressLint
import android.content.res.Resources
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.example.firewallminor.R
import com.example.firewallminor.data.model.Phone
import com.example.firewallminor.data.model.VerificationPhone
import com.example.firewallminor.data.repository.AuthRepository
import com.example.firewallminor.domain.SimpleResult
import com.example.firewallminor.utils.*
import kotlinx.coroutines.launch

class CodeViewModel(private val repository: AuthRepository, private val prefsAuth: PrefsAuth) :
    BaseViewModel() {
    val enabled = NonNullObservableField(false)
    val code = NonNullObservableField("")
    val authCodeHint = NonNullObservableField(SpannableString(""))
    val codeErrorVisibility = NonNullObservableField(View.GONE)
    var resources: Resources? = null
    val phone = NonNullObservableField("")
    val end = SingleLiveData<Int>()
    val timeLeft = NonNullObservableField(SpannableString(""))
    val clickable = NonNullObservableField(false)
    val openMainActivity = SingleLiveData<Boolean>()

    @SuppressLint("StaticFieldLeak")
    lateinit var activity: FragmentActivity

    fun init(resources: Resources, phone: String, requireActivity: FragmentActivity) {
        this.resources = resources
        this.phone.set(phone)
        authCodeHint.set(SpannableString.valueOf(resources.getString(R.string.auth_code_hint)))
        activity = requireActivity
    }

    fun onClick() {
        viewModelScope.launch {
            isLoading.value = true
            val response = repository.verifyRegCode(VerificationPhone(phone.get(), code.get()))
            isLoading.value = false
            when (response) {
                is SimpleResult.Error -> {
                    codeErrorVisibility.set(View.VISIBLE)
                }
                is SimpleResult.NetworkError -> {
                    showNetworkError.value = true
                }
                is SimpleResult.Success -> {
                    prefsAuth.setAuthorized(true)
                    openMainActivity.value = true
                }
            }
        }
    }

    fun onTextChanged(s: Editable) {
        if (s.length == 6) {
            enabled.set(true)
            onClick()
        }
        codeErrorVisibility.set(View.GONE)
        if (s.length == 6) {
            hideKeyboard.value = true
        }
        val spaceSpans: Array<SpaceSpan> = s.getSpans(0, s.length, SpaceSpan::class.java)
        for (span in spaceSpans) {
            s.removeSpan(span)
        }
        val length = s.length
        if (2 <= length) {
            s.setSpan(SpaceSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (3 <= length) {
            s.setSpan(SpaceSpan(), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (4 <= length) {
            s.setSpan(SpaceSpan(), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (5 <= length) {
            s.setSpan(SpaceSpan(), 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (6 <= length) {
            s.setSpan(SpaceSpan(), 5, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        end.value = when (length) {
            0 -> 0
            1 -> 2
            2 -> 4
            3 -> 6
            4 -> 8
            5 -> 10
            else -> 11
        }
    }

    fun sendAgain() {
        viewModelScope.launch {
            isLoading.value = true
            repository.sendRegCodeToPhone(Phone(phone.get()), activity)
            isLoading.value = false
        }
    }
}