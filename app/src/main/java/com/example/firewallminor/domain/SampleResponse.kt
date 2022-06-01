package com.example.firewallminor.domain

import com.example.firewallminor.domain.SimpleResult

class SampleResponse(
    private val success: Boolean = false,
    private val status: Boolean = false,
    private val message: String = "",
) {

    fun getSimpleResult(): SimpleResult<String> {
        return if (success || status) {
            SimpleResult.Success(message)
        } else if (!success) {
            SimpleResult.Error(message)
        } else {
            SimpleResult.NetworkError
        }
    }

    fun getSimpleResultBoolean(): SimpleResult<Boolean> {
        return if (status) {
            SimpleResult.Success(success)
        } else if (!status) {
            SimpleResult.Error(message)
        } else {
            SimpleResult.NetworkError
        }
    }
}

