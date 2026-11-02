package com.example.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

data class FavoriteItem(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val categoryBn: String,
    val categoryEn: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun title(): String = str(titleBn, titleEn)
    fun category(): String = str(categoryBn, categoryEn)
}

object FavoritesManager {
    private const val PREFS_NAME = "router_doc_favorites"
    private const val KEY_FAVORITES = "fav_items"

    val favorites = mutableStateListOf<FavoriteItem>()

    fun init(context: Context) {
        favorites.clear()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_FAVORITES, "[]") ?: "[]"
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                favorites.add(
                    FavoriteItem(
                        id = obj.getString("id"),
                        titleBn = obj.optString("titleBn", ""),
                        titleEn = obj.optString("titleEn", ""),
                        categoryBn = obj.optString("categoryBn", ""),
                        categoryEn = obj.optString("categoryEn", ""),
                        content = obj.optString("content", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore parse errors on empty or corrupted
        }
    }

    fun isFavorite(id: String): Boolean {
        return favorites.any { it.id == id }
    }

    fun toggleFavorite(context: Context, item: FavoriteItem): Boolean {
        val exists = isFavorite(item.id)
        if (exists) {
            favorites.removeAll { it.id == item.id }
        } else {
            favorites.add(0, item)
        }
        persist(context)
        return !exists
    }

    private fun persist(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val array = JSONArray()
        for (item in favorites) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("titleBn", item.titleBn)
            obj.put("titleEn", item.titleEn)
            obj.put("categoryBn", item.categoryBn)
            obj.put("categoryEn", item.categoryEn)
            obj.put("content", item.content)
            obj.put("timestamp", item.timestamp)
            array.put(obj)
        }
        prefs.edit().putString(KEY_FAVORITES, array.toString()).apply()
    }
}
