package com.example.firewallminor.auth

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.example.firewallminor.R
import com.example.firewallminor.data.model.Phone
import com.example.firewallminor.data.repository.AuthRepository
import com.example.firewallminor.domain.SimpleResult
import com.example.firewallminor.utils.*
import kotlinx.coroutines.launch

class PhoneViewModel(private val repository: AuthRepository) : BaseViewModel() {
    val enabled = NonNullObservableField(false)
    val authPhoneHint = NonNullObservableField(SpannableString(""))
    val phone = NonNullObservableField("")
    var resources: Resources? = null
    val end = SingleLiveData<Int>()

    @SuppressLint("StaticFieldLeak")
    private lateinit var activity: FragmentActivity

    fun init(resources: Resources, fragmentActivity: FragmentActivity) {
        this.resources = resources
        activity = fragmentActivity
        authPhoneHint.set(SpannableString.valueOf(resources.getString(R.string.auth_phone_hint)))
    }

    fun onTextChanged(s: Editable) {
        if (s.length > 10) {
            phone.set(s.toString().substring(0, 10))
            return
        }
        enabled.set(s.length == 10)
        if (s.length == 10) {
            hideKeyboard.value = true
        }
        val spaceSpans: Array<SpaceSpan> = s.getSpans(0, s.length, SpaceSpan::class.java)
        val plusSpans: Array<PlusSpan> = s.getSpans(0, s.length, PlusSpan::class.java)
        for (span in spaceSpans) {
            s.removeSpan(span)
        }
        for (span in plusSpans) {
            s.removeSpan(span)
        }
        val length = s.length
        if (4 <= length) {
            s.setSpan(SpaceSpan(), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (7 <= length) {
            s.setSpan(SpaceSpan(), 6, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        end.value = when (length) {
            0 -> 0
            1 -> 1
            2 -> 2
            3 -> 4
            4 -> 5
            5 -> 6
            6 -> 7
            7 -> 9
            8 -> 10
            9 -> 11
            else -> 12
        }
    }

    fun onClick() {
        viewModelScope.launch {
            isLoading.value = true
            val response = repository.sendRegCodeToPhone(Phone(phone.get()), activity)
            isLoading.value = false
            when (response) {
                is SimpleResult.Success -> {
                    navigateWithData.value = Navigation(
                        R.id.action_phone_to_login,
                        destinationActivity = null,
                        Bundle().apply {
                            putString(PHONE, phone.get())
                        })
                }
                is SimpleResult.Error -> {
                    errorString.value = response.errorMessage
                }
                is SimpleResult.NetworkError -> {
                    showNetworkError.value = true
                }
            }
        }
    }

    companion object {
        const val PHONE = "PHONE"
    }
}