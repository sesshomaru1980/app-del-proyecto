package com.example.neobarber.core.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("neo_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String?) {
        if (!token.isNullOrBlank()) {
            prefs.edit().putString("token", token).apply()
        }
    }

    fun saveRole(role: String?) {
        if (!role.isNullOrBlank()) {
            prefs.edit().putString("role", role).apply()
        }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun getRole(): String? = prefs.getString("role", null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}