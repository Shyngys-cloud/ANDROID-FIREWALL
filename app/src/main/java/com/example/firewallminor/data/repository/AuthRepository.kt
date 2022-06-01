package com.example.firewallminor.data.repository

import android.app.Activity
import com.example.firewallminor.data.model.Phone
import com.example.firewallminor.data.model.VerificationPhone
import com.example.firewallminor.domain.SampleResponse
import com.example.firewallminor.domain.SimpleResult
import com.example.firewallminor.utils.await
import com.example.firewallminor.utils.phoneServerFormat
import com.example.firewallminor.utils.simpleError
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class AuthRepository(
//    private val prefs: PrefsAuth,
    private val auth: FirebaseAuth = Firebase.auth,
//    private val userStore: CollectionReference = Firebase.firestore.collection(USERS)
) {

    private var storedVerificationId: String = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                println("onVerificationCompleted")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                println("onVerificationFailed")
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                    }
                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                    }
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                println("onCodeSent")
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }

    suspend fun sendRegCodeToPhone(mobile: Phone, activity: Activity):
            SimpleResult<Boolean> {
        return try {
            println(mobile.Phone.phoneServerFormat())
            withContext(Dispatchers.IO) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(mobile.Phone.phoneServerFormat())       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .setActivity(activity)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
                SampleResponse(success = true, status = true, message = "").getSimpleResultBoolean()
            }
        } catch (e: Throwable) {
            e.simpleError()
        }
    }

    suspend fun verifyRegCode(verificationPhone: VerificationPhone):
            SimpleResult<String> {
        return try {
            withContext(Dispatchers.IO) {
                auth.signInWithCredential(
                    PhoneAuthProvider.getCredential(
                        storedVerificationId,
                        verificationPhone.Code.toString()
                    )
                ).await()
            }
        } catch (e: Throwable) {
            e.simpleError()
        }
    }

//    suspend fun authByPhone(mobile: AuthPhone):
//            SimpleResult<Boolean> {
//        return try {
//            withContext(Dispatchers.IO) {
//                var status = true
//                var success = false
//                var localizedMessage = ""
//                val task = userStore.get()
//                suspendCancellableCoroutine { cont ->
//                    task.addOnCompleteListener {
//                        when {
//                            task.isSuccessful -> {
//                                // Sign in success, update UI with the signed-in user's information
//                                for (document in task.result) {
//                                    val user = document.toObject(UserFB::class.java)
//                                    if (user.phone == mobile.Phone && user.password == mobile.Password)
//                                        success = true
//                                }
//                            }
//                            else -> {
//                                status = false
//                                task.exception?.localizedMessage?.let {
//                                    localizedMessage = it
//                                }
//                            }
//                        }
//                        cont.resume(
//                            SampleResponse(
//                                success = success,
//                                status = status,
//                                message = localizedMessage
//                            ).getSimpleResultBoolean()
//                        )
//                    }
//                }
//            }
//        } catch (e: Throwable) {
//            e.simpleError()
//        }
//    }

//    suspend fun createUser(user: UserFB):
//            SimpleResult<String> {
//        return try {
//            withContext(Dispatchers.IO) {
//                userStore.add(user).await()
//            }
//        } catch (e: Throwable) {
//            e.simpleError()
//        }
//    }

//    suspend fun checkIfUserExists(
//        mobile: Phone
//    ): SimpleResult<Boolean> {
//        return try {
//            withContext(Dispatchers.IO) {
//                var status = true
//                var success = false
//                var localizedMessage = ""
//                val task = userStore.get()
//                suspendCancellableCoroutine { cont ->
//                    task.addOnCompleteListener {
//                        when {
//                            task.isSuccessful -> {
//                                // Sign in success, update UI with the signed-in user's information
//                                for (document in task.result) {
//                                    val user = document.toObject(UserFB::class.java)
//                                    if (user.phone == mobile.Phone)
//                                        success = true
//                                }
//                            }
//                            else -> {
//                                status = false
//                                task.exception?.localizedMessage?.let {
//                                    localizedMessage = it
//                                }
//                            }
//                        }
//                        cont.resume(
//                            SampleResponse(
//                                success = success,
//                                status = status,
//                                message = localizedMessage
//                            ).getSimpleResultBoolean()
//                        )
//                    }
//                }
//            }
//        } catch (e: Throwable) {
//            e.simpleError()
//        }
//    }

//    suspend fun verifyForgotPhone(verificationPhone: VerificationPhone):
//            SimpleResult<String> {
//        return try {
//            withContext(Dispatchers.IO) {
//                service.verifyForgotPwdCodeAsync(
//                    prefs.getLang(), verificationPhone
//                ).await()
//                    .getSimpleResult()
//            }
//        } catch (e: Throwable) {
//            e.simpleError()
//        }
//    }
}