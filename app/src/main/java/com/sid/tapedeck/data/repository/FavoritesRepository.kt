package com.sid.tapedeck.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tapedeck_favorites")

class FavoritesRepository(private val context: Context) {
    private val favoritesKey = stringSetPreferencesKey("favorite_song_ids")

    val favoriteSongIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[favoritesKey] ?: emptySet()
    }

    suspend fun toggleFavorite(songId: Long) {
        val idStr = songId.toString()
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey] ?: emptySet()
            if (current.contains(idStr)) {
                prefs[favoritesKey] = current - idStr
            } else {
                prefs[favoritesKey] = current + idStr
            }
        }
    }

    suspend fun isFavorite(songId: Long): Boolean {
        var isFav = false
        context.dataStore.edit { prefs ->
            isFav = (prefs[favoritesKey] ?: emptySet()).contains(songId.toString())
        }
        return isFav
    }
}

