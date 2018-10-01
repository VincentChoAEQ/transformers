package com.awestruck.transformers.util

import android.app.Activity
import android.content.SharedPreferences
import com.awestruck.transformers.App

/**
 * Created by Chris on 2018-09-29.
 */
object Preferences {

    private const val PREF = "PREF"
    private const val AUTH_TOKEN = "AUTH_TOKEN"

    private val preferences: SharedPreferences = App.application.getSharedPreferences(PREF, Activity.MODE_PRIVATE)

    var token: String?
        get() = preferences.getString(AUTH_TOKEN, null)
        set(value) = preferences.edit().putString(AUTH_TOKEN, value).apply()

    val isNewUser: Boolean
        get() = token == null


}