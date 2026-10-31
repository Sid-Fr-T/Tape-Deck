package com.sid.tapedeck.data.repository

import android.content.Context
import com.sid.tapedeck.data.model.Album
import com.sid.tapedeck.data.model.Artist
import com.sid.tapedeck.data.model.Song
import com.sid.tapedeck.data.scanner.AudioScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed interface LibraryState {
    data object Idle : LibraryState
    data object Loading : LibraryState
    data class Success(
        val songs: List<Song>,
        val albums: List<Album>,
        val artists: List<Artist>
    ) : LibraryState
    data class Empty(val message: String = "No audio cassettes found on device storage.") : LibraryState
    data class Error(val throwable: Throwable) : LibraryState
}

class MusicRepository(private val context: Context) {
    private val scanner = AudioScanner(context.applicationContext)

    private val _libraryState = MutableStateFlow<LibraryState>(LibraryState.Idle)
    val libraryState: StateFlow<LibraryState> = _libraryState.asStateFlow()

    suspend fun scanMusic(force: Boolean = false) {
        if (!force && _libraryState.value is LibraryState.Success) {
            return
        }

        _libraryState.value = LibraryState.Loading

        withContext(Dispatchers.IO) {
            try {
                val library = scanner.scan()
                if (library.songs.isEmpty()) {
                    _libraryState.value = LibraryState.Empty()
                } else {
                    _libraryState.value = LibraryState.Success(
                        songs = library.songs,
                        albums = library.albums,
                        artists = library.artists
                    )
                }
            } catch (e: Throwable) {
                _libraryState.value = LibraryState.Error(e)
            }
        }
    }
}

