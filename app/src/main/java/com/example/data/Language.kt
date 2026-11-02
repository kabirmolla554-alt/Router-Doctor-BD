package com.example.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage {
    BN, EN
}

object LanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.BN)
        private set

    private const val PREF_NAME = "router_doc_lang"
    private const val KEY_LANG = "selected_lang"

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_LANG, AppLanguage.BN.name)
        currentLanguage = if (saved == AppLanguage.EN.name) AppLanguage.EN else AppLanguage.BN
    }

    fun toggleLanguage(context: Context) {
        setLanguage(context, if (currentLanguage == AppLanguage.BN) AppLanguage.EN else AppLanguage.BN)
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        currentLanguage = language
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, language.name).apply()
    }
}

// Convenient helper for bilingual texts
fun str(bn: String, en: String): String {
    return if (LanguageManager.currentLanguage == AppLanguage.BN) bn else en
}
