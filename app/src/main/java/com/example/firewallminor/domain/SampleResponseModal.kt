package kz.litro.data.model

import com.example.firewallminor.domain.SimpleResult

class SampleResponseModal<T>(
    private val success: Boolean = false,
    private val status: Boolean = false,
    private val message: String = "",
    val data: T? = null,
    val endSession: Boolean = false,
    private val msgType: String? = null,
) {

    fun getSimpleResult(): SimpleResult<T> {
        return if (success || status) {
            SimpleResult.Success(data!!)
        } else if (!success && msgType == "426") {
            SimpleResult.NetworkError
        } else {
            SimpleResult.Error(message)
        }
    }
}

