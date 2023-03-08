package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EncryptedPreferenceManager @Inject constructor(@ApplicationContext private val applicationContext: Context) {

    private val encryptedPref by lazy { createSharedPreferences() }

    private fun createSharedPreferences(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            "login_info_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun writeEncrypted(key: String, value: String) {
        encryptedPref.edit().apply {
            putString(key, value)
            apply()
        }
    }

    fun readEncrypted(key: String): String? {
        return encryptedPref.getString(key, null)
    }

    companion object {
        const val USER_ID = "user_id"
        const val PASSWORD = "password"
    }
}