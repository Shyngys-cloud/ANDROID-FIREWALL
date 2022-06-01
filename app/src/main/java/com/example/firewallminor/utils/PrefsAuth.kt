package com.example.firewallminor.utils

import android.content.Context

/*  Storage for token, pin and other user info data  */
class PrefsAuth(context: Context) : BasePrefs(context) {

    companion object {
        private const val TAG = "PrefsAuth"
        private const val IS_FIRST = "isFirst"
        private const val IS_AUTHORIZED = "isAuthorized"
        private const val IS_MASTER = "isMaster"
        private const val LOGIN = "login"
        private const val FULL_NAME = "fullName"
        private const val BIOMETRICS = "biometrics"
        private const val PUSH_NOTIFICATION = "PUSH"
        private const val ASK_PERMISSION = "ask_permission"
        private const val DEVICE_ID = "deviceID"
        private const val TOKEN_DATA = "TOKEN_DATA"
        private const val PROMOCODE = "promocode"
        private const val AUTH_TOKEN = "authToken"
        private const val CHECK_NEED = "checkNeed"
        private const val CITY_ID = "city_id"
        private const val ACCESS_TOKEN = "accessToken"
        private const val USER = "user"
        private const val PRODUCT = "product"
        private const val USER_PHONE_NUMBER: String = "USER_PHONE_NUMBER"
    }

    fun isFirst(): Boolean = prefs?.getBoolean(IS_FIRST, true) ?: true


    fun saveUserPhone(phone: String) =
        prefs?.edit()?.putString(USER_PHONE_NUMBER, phone)?.apply()

    fun saveDeviceId(deviceId: String) =
        prefs?.edit()?.putString(DEVICE_ID, deviceId)?.apply()

    fun saveAuthToken(authToken: String) =
        prefs?.edit()?.putString(AUTH_TOKEN, authToken)?.apply()

    fun savePromo(promo: String) =
        prefs?.edit()?.putString(PROMOCODE, promo)?.apply()

    fun setBiometricsEnabled(value: Boolean) = prefs?.edit()?.putBoolean(BIOMETRICS, value)?.apply()
    fun setPushEnabled(value: Boolean) =
        prefs?.edit()?.putBoolean(PUSH_NOTIFICATION, value)?.apply()

    fun setTouchPermission(value: Boolean) =
        prefs?.edit()?.putBoolean(ASK_PERMISSION, value)?.apply()

    fun setCityId(cityId: Int) =
        prefs?.edit()?.putInt(CITY_ID, cityId)?.apply()

    fun saveAccessToken(accessToken: String) {
        setTouchPermission(true)
        prefs?.edit()?.putString(ACCESS_TOKEN, accessToken)?.apply()
    }

    fun getUserPhone(): String? = prefs?.getString(USER_PHONE_NUMBER, null)
    fun getAccessToken(): String? = prefs?.getString(ACCESS_TOKEN, null)
    fun getAuthToken(): String? = prefs?.getString(AUTH_TOKEN, null)
    fun getDeviceId(): String? = prefs?.getString(DEVICE_ID, null)

    fun logout() {
        setAuthorized(false)
    }

    fun clear() = prefs?.edit()?.clear()?.apply()

    fun getLogin(): String? = prefs?.getString(LOGIN, null)
    fun getPromo(): String? = prefs?.getString(PROMOCODE, null)
    fun isBiometricsEnabled(): Boolean = prefs?.getBoolean(BIOMETRICS, false) ?: false
    fun isPushEnabled(): Boolean = prefs?.getBoolean(PUSH_NOTIFICATION, false) ?: false
    fun showTouchPermission(): Boolean = prefs?.getBoolean(ASK_PERMISSION, true) ?: true


    //  Get full name user
    fun getFullName(): String = prefs?.getString(FULL_NAME, "") ?: ""
    fun getCityId(): Int = prefs?.getInt(CITY_ID, 0) ?: 0

    //  Check, if user authorized or not
    fun isAuthorized(): Boolean = prefs?.getBoolean(IS_AUTHORIZED, false) ?: false
    fun setAuthorized(isAuthorized: Boolean) =
        prefs?.edit()?.putBoolean(IS_AUTHORIZED, isAuthorized)?.apply()

    //  Check, if user master or not
    fun isMaster(): Boolean = prefs?.getBoolean(IS_MASTER, false) ?: false
    fun setMaster(isMaster: Boolean) =
        prefs?.edit()?.putBoolean(IS_MASTER, isMaster)?.apply()


    private fun checkNeed(): Boolean {
        return prefs?.getBoolean(CHECK_NEED, true) ?: true
    }

    private fun setCheckNeed() = prefs?.edit()?.putBoolean(CHECK_NEED, false)?.apply()


}