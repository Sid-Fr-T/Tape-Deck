package com.sid.tapedeck.player

import android.app.Application
import android.content.ComponentName
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sid.tapedeck.data.model.Song
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    // In-memory song mapping for quick lookup by mediaId
    private val songsCache = mutableMapOf<String, Song>()

    // Exposed StateFlows
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _shuffleModeEnabled = MutableStateFlow(false)
    val shuffleModeEnabled: StateFlow<Boolean> = _shuffleModeEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    // 2Hz Position update job - active ONLY when UI is visible
    private var positionPollingJob: Job? = null
    private var isUiCurrentlyVisible = false

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateCurrentSongFromMediaItem(mediaItem)
            updateDuration()
            _currentPositionMs.value = controller?.currentPosition?.coerceAtLeast(0L) ?: 0L
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying && isUiCurrentlyVisible) {
                startPositionPolling()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateDuration()
            _currentPositionMs.value = controller?.currentPosition?.coerceAtLeast(0L) ?: 0L
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _shuffleModeEnabled.value = shuffleModeEnabled
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _repeatMode.value = repeatMode
        }

        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
            syncQueueFromController()
            updateDuration()
        }
    }

    init {
        initializeMediaController(application)
    }

    private fun initializeMediaController(application: Application) {
        val sessionToken = SessionToken(
            application,
            ComponentName(application, PlaybackService::class.java)
        )
        val future = MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture = future

        future.addListener(
            {
                try {
                    val mediaController = future.get()
                    controller = mediaController
                    mediaController.addListener(playerListener)

                    // Sync initial state
                    _isPlaying.value = mediaController.isPlaying
                    _shuffleModeEnabled.value = mediaController.shuffleModeEnabled
                    _repeatMode.value = mediaController.repeatMode
                    updateCurrentSongFromMediaItem(mediaController.currentMediaItem)
                    updateDuration()
                    syncQueueFromController()

                    if (mediaController.isPlaying && isUiCurrentlyVisible) {
                        startPositionPolling()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            ContextCompat.getMainExecutor(application)
        )
    }

    /**
     * UI Visibility hook: Updates position ~2 times per second only while UI is visible.
     */
    fun setUiVisible(visible: Boolean) {
        isUiCurrentlyVisible = visible
        if (visible) {
            startPositionPolling()
        } else {
            stopPositionPolling()
        }
    }

    private fun startPositionPolling() {
        if (positionPollingJob?.isActive == true) return
        positionPollingJob = viewModelScope.launch {
            while (isActive && isUiCurrentlyVisible) {
                controller?.let { player ->
                    if (player.isPlaying) {
                        _currentPositionMs.value = player.currentPosition.coerceAtLeast(0L)
                    }
                }
                delay(500) // ~2 times per second
            }
        }
    }

    private fun stopPositionPolling() {
        positionPollingJob?.cancel()
        positionPollingJob = null
    }

    // --- Actions ---

    fun playList(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        val player = controller ?: return

        songs.forEach { songsCache[it.id.toString()] = it }

        val mediaItems = songs.map { it.toMediaItem() }
        val safeIndex = startIndex.coerceIn(0, songs.size - 1)

        player.setMediaItems(mediaItems, safeIndex, 0L)
        player.prepare()
        player.play()

        _queue.value = songs
        _currentSong.value = songs[safeIndex]
    }

    fun playPause() {
        val player = controller ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
        }
    }

    fun next() {
        val player = controller ?: return
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }

    fun previous() {
        val player = controller ?: return
        if (player.currentPosition > 3000L) {
            player.seekTo(0L)
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        } else {
            player.seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    fun toggleShuffle() {
        val player = controller ?: return
        player.shuffleModeEnabled = !player.shuffleModeEnabled
    }

    fun cycleRepeatMode() {
        val player = controller ?: return
        val nextMode = when (player.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        player.repeatMode = nextMode
    }

    fun addToQueue(song: Song) {
        songsCache[song.id.toString()] = song
        controller?.addMediaItem(song.toMediaItem())
        _queue.value = _queue.value + song
    }

    fun playNext(song: Song) {
        val player = controller ?: return
        songsCache[song.id.toString()] = song
        val nextIndex = (player.currentMediaItemIndex + 1).coerceAtMost(player.mediaItemCount)
        player.addMediaItem(nextIndex, song.toMediaItem())
        val updated = _queue.value.toMutableList().apply { add(nextIndex, song) }
        _queue.value = updated
    }

    fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val player = controller ?: return
        if (fromIndex in 0 until player.mediaItemCount && toIndex in 0 until player.mediaItemCount) {
            player.moveMediaItem(fromIndex, toIndex)
            val updated = _queue.value.toMutableList()
            val item = updated.removeAt(fromIndex)
            updated.add(toIndex, item)
            _queue.value = updated
        }
    }

    fun removeFromQueue(index: Int) {
        val player = controller ?: return
        if (index in 0 until player.mediaItemCount) {
            player.removeMediaItem(index)
            val updated = _queue.value.toMutableList().apply { removeAt(index) }
            _queue.value = updated
        }
    }

    private fun updateCurrentSongFromMediaItem(mediaItem: MediaItem?) {
        val id = mediaItem?.mediaId ?: return
        _currentSong.value = songsCache[id] ?: _queue.value.firstOrNull { it.id.toString() == id }
    }

    private fun updateDuration() {
        val player = controller ?: return
        val dur = player.duration
        _durationMs.value = if (dur > 0L) dur else _currentSong.value?.durationMs ?: 0L
    }

    private fun syncQueueFromController() {
        val player = controller ?: return
        val count = player.mediaItemCount
        val items = mutableListOf<Song>()
        for (i in 0 until count) {
            val item = player.getMediaItemAt(i)
            val song = songsCache[item.mediaId] ?: _queue.value.firstOrNull { it.id.toString() == item.mediaId }
            if (song != null) {
                items.add(song)
            }
        }
        if (items.isNotEmpty()) {
            _queue.value = items
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPositionPolling()
        controller?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
        controllerFuture = null
    }

    private fun Song.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(contentUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(albumArtUri)
                    .build()
            )
            .build()
    }
}

