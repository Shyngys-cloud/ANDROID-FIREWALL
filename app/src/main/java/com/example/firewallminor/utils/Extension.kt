package com.example.firewallminor.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.example.firewallminor.domain.SampleResponse
import com.example.firewallminor.domain.SimpleResult
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONException
import java.text.SimpleDateFormat
import kotlin.coroutines.resume


fun Context.makeToast(message: String) {
    if (message.isNotBlank())
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.getStyledResourceId(id: Int): Int {
    val attrs = intArrayOf(id)
    val typedArray = this.theme.obtainStyledAttributes(attrs)
    return typedArray.getResourceId(0, android.R.color.black)
}

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

@SuppressLint("SimpleDateFormat")
fun String?.dateFormat(): String {
    return this?.let {
        try {
            SimpleDateFormat("dd/MM").format(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    it.substring(0, 19)
                )!!
            )
        } catch (e: Exception) {
            ""
        }
    } ?: ""
}

fun View.hideView(isOpen: Boolean) {
    this.animate().scaleX(if (isOpen) 0f else 1f).scaleY(if (isOpen) 0f else 1f).setDuration(300)
        .start()
}

fun View.visible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun String?.emailIsValid(): Boolean {
    if (this == null) {
        return false
    }
    return !TextUtils.isEmpty(this.trim()) && android.util.Patterns.EMAIL_ADDRESS.matcher(this.trim())
        .matches()
}

fun String.underLine(): SpannableString {
    val spanStr = SpannableString(this)
    spanStr.setSpan(UnderlineSpan(), 0, spanStr.length, 0)
    return spanStr
}

fun String.phoneServerFormat(): String {
    return "+7$this"
}

fun <T> Throwable.simpleError(): SimpleResult<T> {
    this.printStackTrace()
    return when (this) {
        is JSONException -> {
            SimpleResult.Error("Что-то пошло не так")
        }
        is IllegalStateException -> {
            SimpleResult.Error("Что-то пошло не так")
        }
        else -> {
            SimpleResult.NetworkError
        }
    }
}

suspend fun <T> Task<T>.await(): SimpleResult<String> {
    var success: Boolean
    var status: Boolean
    var message = ""

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    // Sign in success, update UI with the signed-in user's information
                    success = true
                    status = true
                }
                else -> {
                    status = false
                    success = false
                    task.exception?.localizedMessage?.let {
                        message = it
                        println(message)
                    }
                }
            }
            cont.resume(
                SampleResponse(
                    success = success,
                    status = status,
                    message = message
                ).getSimpleResult()
            )
        }
    }
}