package com.example.resturantproject.helpers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val APP_PREF = "MealsApp"
    private val APP_PREF_EMAIL = "email"

    private val preferences: SharedPreferences =
        context.getSharedPreferences(APP_PREF, MODE_PRIVATE)

    var emailPref: String?
        get() = preferences.getString(APP_PREF_EMAIL, null)
        set(value) = preferences.edit().putString(APP_PREF_EMAIL, value).apply()
}