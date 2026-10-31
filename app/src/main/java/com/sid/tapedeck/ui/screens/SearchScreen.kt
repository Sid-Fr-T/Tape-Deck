package com.sid.tapedeck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.data.model.Song
import com.sid.tapedeck.data.repository.LibraryState
import com.sid.tapedeck.ui.components.AlbumArt
import com.sid.tapedeck.ui.components.SongRow
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeChip
import com.sid.tapedeck.ui.components.TapeChipRow
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType
import com.sid.tapedeck.ui.theme.chunkyBorder
import com.sid.tapedeck.ui.theme.hardShadow

private enum class SearchFilter(val label: String) {
    All("All"),
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists")
}

@Composable
fun SearchScreen(
    libraryState: LibraryState,
    currentPlayingSong: Song?,
    isPlaying: Boolean,
    onPlaySong: (Song, List<Song>) -> Unit,
    onNavigateToAlbum: (String) -> Unit
) {
    val colors = TapeTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SearchFilter.All) }
    val focusRequester = remember { FocusRequester() }

    val allSongs = (libraryState as? LibraryState.Success)?.songs ?: emptyList()
    val allAlbums = (libraryState as? LibraryState.Success)?.albums ?: emptyList()

    // Filter in memory as you type, case-insensitive
    val queryTrimmed = searchQuery.trim().lowercase()

    val matchingSongs = remember(allSongs, queryTrimmed) {
        if (queryTrimmed.isEmpty()) {
            allSongs
        } else {
            allSongs.filter { song ->
                song.title.lowercase().contains(queryTrimmed) ||
                    song.artist.lowercase().contains(queryTrimmed) ||
                    song.album.lowercase().contains(queryTrimmed)
            }
        }
    }

    val matchingAlbums = remember(allAlbums, queryTrimmed) {
        if (queryTrimmed.isEmpty()) {
            allAlbums
        } else {
            allAlbums.filter { album ->
                album.title.lowercase().contains(queryTrimmed) ||
                    album.artist.lowercase().contains(queryTrimmed)
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 56dp Pill Search Field (radius 28dp, 3dp border)
        val pillShape = RoundedCornerShape(28.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .height(56.dp)
                .hardShadow(color = colors.line, offsetY = 4.dp, shape = pillShape)
                .background(color = colors.card, shape = pillShape)
                .chunkyBorder(color = colors.line, width = 3.dp, shape = pillShape)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = TapeIcons.Search,
                    contentDescription = null,
                    tint = colors.ink,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        TapeText(
                            text = "Search offline songs, artists, albums...",
                            style = TapeType.rowTitle,
                            color = colors.muted
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = TapeType.rowTitle.copy(color = colors.ink),
                        cursorBrush = SolidColor(colors.pink),
                        singleLine = true
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(colors.card, CircleShape)
                            .chunkyBorder(colors.line, width = 2.dp, shape = CircleShape)
                            .clickable { searchQuery = "" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = TapeIcons.Close,
                            contentDescription = "Clear search",
                            tint = colors.ink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chips: All, Songs, Albums, Artists
        TapeChipRow(
            labels = listOf("All", "Songs", "Albums", "Artists"),
            selected = selectedFilter.label,
            onSelect = { label ->
                SearchFilter.entries.firstOrNull { it.label == label }?.let { selectedFilter = it }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Results grouped under "SONGS" and "ALBUMS" section headers (Space Mono 18sp)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Group 1: SONGS
            if (selectedFilter == SearchFilter.All || selectedFilter == SearchFilter.Songs) {
                if (matchingSongs.isNotEmpty()) {
                    item {
                        TapeText(
                            text = "SONGS",
                            style = TapeType.section,
                            color = colors.ink
                        )
                    }

                    items(matchingSongs, key = { it.id }) { song ->
                        val isCurrentPlaying = isPlaying && currentPlayingSong?.id == song.id
                        SongRow(
                            title = song.title,
                            artist = song.artist,
                            duration = song.durationFormatted,
                            albumId = song.albumId.toString(),
                            artworkUri = song.albumArtUri,
                            isPlaying = isCurrentPlaying,
                            onClick = { onPlaySong(song, matchingSongs) }
                        )
                    }
                }
            }

            // Group 2: ALBUMS
            if (selectedFilter == SearchFilter.All || selectedFilter == SearchFilter.Albums) {
                if (matchingAlbums.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        TapeText(
                            text = "ALBUMS",
                            style = TapeType.section,
                            color = colors.ink
                        )
                    }

                    items(matchingAlbums, key = { it.id }) { album ->
                        TapeCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(66.dp)
                                .clickable { onNavigateToAlbum(album.id.toString()) },
                            radius = 18.dp,
                            shadowOffset = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AlbumArt(
                                    albumId = album.id.toString(),
                                    artworkUri = album.albumArtUri,
                                    size = 46.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    TapeText(
                                        text = album.title,
                                        style = TapeType.rowTitle,
                                        color = colors.ink,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    TapeText(
                                        text = "Album • ${album.artist}",
                                        style = TapeType.subtitle,
                                        color = colors.muted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Icon(
                                    imageVector = TapeIcons.Chevron,
                                    contentDescription = null,
                                    tint = colors.ink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Empty state if nothing matches query
            if (matchingSongs.isEmpty() && matchingAlbums.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TapeCard(
                            modifier = Modifier.fillMaxWidth(),
                            radius = 18.dp,
                            shadowOffset = 4.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TapeText(
                                    text = "NO MATCHING CASSETTES",
                                    style = TapeType.section,
                                    color = colors.pinkText
                                )
                                TapeText(
                                    text = "Try searching for a different song title, artist, or album name.",
                                    style = TapeType.body,
                                    color = colors.muted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
