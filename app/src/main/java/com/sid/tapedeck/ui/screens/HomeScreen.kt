package com.sid.tapedeck.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sid.tapedeck.data.model.Song
import com.sid.tapedeck.data.repository.LibraryState
import com.sid.tapedeck.ui.components.AlbumArt
import com.sid.tapedeck.ui.components.CassetteLogo
import com.sid.tapedeck.ui.components.SongRow
import com.sid.tapedeck.ui.components.TapeButton
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeChip
import com.sid.tapedeck.ui.components.TapeChipRow
import com.sid.tapedeck.ui.components.TapeIconButton
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType

private enum class HomeFilter(val label: String) {
    All("All"),
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists")
}

@Composable
fun HomeScreen(
    libraryState: LibraryState,
    currentPlayingSong: Song?,
    isPlaying: Boolean,
    onPlaySong: (Song, List<Song>) -> Unit,
    onShuffleAll: (List<Song>) -> Unit,
    onRescan: () -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    val colors = TapeTheme.colors
    var selectedFilter by remember { mutableStateOf(HomeFilter.All) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header Row (52dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CassetteLogo()
                TapeText(
                    text = "TAPEDECK",
                    style = TapeType.header,
                    color = colors.ink
                )
            }

            TapeIconButton(
                icon = TapeIcons.Search,
                size = 46.dp,
                backgroundColor = colors.card,
                iconTint = colors.ink,
                shadowDistance = 4.dp,
                onClick = onNavigateToSearch,
                contentDescription = "Search"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (libraryState) {
            is LibraryState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TapeCard(
                        modifier = Modifier.fillMaxWidth(),
                        radius = 22.dp,
                        shadowOffset = 5.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TapeText(
                                text = "INDEXING CASSETTES...",
                                style = TapeType.header,
                                color = colors.ink
                            )
                            TapeText(
                                text = "Reading audio metadata from phone storage.",
                                style = TapeType.body,
                                color = colors.muted
                            )
                        }
                    }
                }
            }

            is LibraryState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TapeCard(
                        modifier = Modifier.fillMaxWidth(),
                        radius = 22.dp,
                        shadowOffset = 5.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TapeText(
                                text = "NO CASSETTES FOUND",
                                style = TapeType.header,
                                color = colors.ink
                            )
                            TapeText(
                                text = "Add audio files (>30s) to your Music storage to load your offline library.",
                                style = TapeType.body,
                                color = colors.muted
                            )
                            TapeButton(
                                text = "RE-SCAN STORAGE",
                                leadingIcon = TapeIcons.Repeat,
                                onClick = onRescan
                            )
                        }
                    }
                }
            }

            is LibraryState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TapeCard(
                        modifier = Modifier.fillMaxWidth(),
                        radius = 22.dp,
                        shadowOffset = 5.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TapeText(
                                text = "READ ERROR",
                                style = TapeType.header,
                                color = colors.pinkText
                            )
                            TapeText(
                                text = libraryState.throwable.localizedMessage ?: "Failed to query MediaStore",
                                style = TapeType.body,
                                color = colors.ink
                            )
                            TapeButton(
                                text = "RETRY SCAN",
                                leadingIcon = TapeIcons.Repeat,
                                onClick = onRescan
                            )
                        }
                    }
                }
            }

            is LibraryState.Success, LibraryState.Idle -> {
                val songs = (libraryState as? LibraryState.Success)?.songs ?: emptyList()
                val albums = (libraryState as? LibraryState.Success)?.albums ?: emptyList()
                val firstSong = songs.firstOrNull()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Hero TapeCard (padding 16dp)
                    item {
                        Box(modifier = Modifier.padding(horizontal = 22.dp)) {
                            TapeCard(
                                modifier = Modifier.fillMaxWidth(),
                                radius = 22.dp,
                                shadowOffset = 5.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AlbumArt(
                                            albumId = firstSong?.albumId?.toString() ?: "hero",
                                            artworkUri = firstSong?.albumArtUri,
                                            size = 84.dp
                                        )

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            TapeText(
                                                text = "${songs.size} SONGS. ZERO WIFI.",
                                                style = TapeType.section,
                                                color = colors.ink
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            TapeText(
                                                text = "All of it plays right here on your phone. No account, no data.",
                                                style = TapeType.subtitle,
                                                color = colors.muted
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    TapeButton(
                                        text = "Shuffle all",
                                        leadingIcon = TapeIcons.Shuffle,
                                        backgroundColor = colors.pink,
                                        textColor = colors.onPink,
                                        onClick = {
                                            if (songs.isNotEmpty()) {
                                                onShuffleAll(songs)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Chips row: All, Songs, Albums, Artists
                    item {
                        TapeChipRow(
                            labels = listOf("All", "Songs", "Albums", "Artists"),
                            selected = selectedFilter.label,
                            onSelect = { label ->
                                HomeFilter.entries.firstOrNull { it.label == label }?.let { selectedFilter = it }
                            }
                        )
                    }

                    // Section based on filter
                    when (selectedFilter) {
                        HomeFilter.All -> {
                            // Section "JUMP BACK IN"
                            item {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 22.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TapeText(
                                            text = "JUMP BACK IN",
                                            style = TapeType.section,
                                            color = colors.ink
                                        )
                                        TapeText(
                                            text = "See all",
                                            style = TapeType.rowTitle,
                                            color = colors.pinkText,
                                            modifier = Modifier.clickable { onNavigateToLibrary() }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    val recentAlbums = remember(albums) {
                                        albums.sortedByDescending { album ->
                                            album.songs.maxOfOrNull { it.dateAdded } ?: 0L
                                        }.take(8)
                                    }

                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 22.dp),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        items(recentAlbums, key = { it.id }) { album ->
                                            TapeCard(
                                                modifier = Modifier
                                                    .width(150.dp)
                                                    .clickable { onNavigateToAlbum(album.id.toString()) },
                                                radius = 20.dp,
                                                shadowOffset = 5.dp
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp)
                                                ) {
                                                    AlbumArt(
                                                        albumId = album.id.toString(),
                                                        artworkUri = album.albumArtUri,
                                                        size = 126.dp
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    TapeText(
                                                        text = album.title,
                                                        style = TapeType.rowTitle,
                                                        color = colors.ink,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    TapeText(
                                                        text = album.artist,
                                                        style = TapeType.subtitle,
                                                        color = colors.muted,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Recent Songs header
                            item {
                                Box(modifier = Modifier.padding(horizontal = 22.dp)) {
                                    TapeText(
                                        text = "RECENT CASSETTES",
                                        style = TapeType.section,
                                        color = colors.ink
                                    )
                                }
                            }

                            // Recent songs list (12dp gap)
                            items(songs.take(15), key = { it.id }) { song ->
                                Box(modifier = Modifier.padding(horizontal = 22.dp)) {
                                    SongRow(
                                        title = song.title,
                                        artist = song.artist,
                                        duration = song.durationFormatted,
                                        albumId = song.albumId.toString(),
                                        artworkUri = song.albumArtUri,
                                        isPlaying = isPlaying && currentPlayingSong?.id == song.id,
                                        onClick = { onPlaySong(song, songs) }
                                    )
                                }
                            }
                        }

                        HomeFilter.Songs -> {
                            items(songs, key = { it.id }) { song ->
                                Box(modifier = Modifier.padding(horizontal = 22.dp)) {
                                    SongRow(
                                        title = song.title,
                                        artist = song.artist,
                                        duration = song.durationFormatted,
                                        albumId = song.albumId.toString(),
                                        artworkUri = song.albumArtUri,
                                        isPlaying = isPlaying && currentPlayingSong?.id == song.id,
                                        onClick = { onPlaySong(song, songs) }
                                    )
                                }
                            }
                        }

                        HomeFilter.Albums -> {
                            items(albums, key = { it.id }) { album ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 22.dp)
                                        .clickable { onNavigateToAlbum(album.id.toString()) }
                                ) {
                                    TapeCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        radius = 18.dp,
                                        shadowOffset = 4.dp
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AlbumArt(
                                                albumId = album.id.toString(),
                                                artworkUri = album.albumArtUri,
                                                size = 54.dp
                                            )
                                            Spacer(modifier = Modifier.width(14.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                TapeText(
                                                    text = album.title,
                                                    style = TapeType.rowTitle,
                                                    color = colors.ink,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                TapeText(
                                                    text = "${album.artist} • ${album.songCount} tracks",
                                                    style = TapeType.subtitle,
                                                    color = colors.muted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HomeFilter.Artists -> {
                            val artists = (libraryState as? LibraryState.Success)?.artists ?: emptyList()
                            items(artists, key = { it.name }) { artist ->
                                Box(modifier = Modifier.padding(horizontal = 22.dp)) {
                                    TapeCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        radius = 18.dp,
                                        shadowOffset = 4.dp
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                TapeText(
                                                    text = artist.name,
                                                    style = TapeType.rowTitle,
                                                    color = colors.ink
                                                )
                                                TapeText(
                                                    text = "${artist.albumCount} Albums • ${artist.songCount} Tracks",
                                                    style = TapeType.subtitle,
                                                    color = colors.muted
                                                )
                                            }
                                            TapeIconButton(
                                                icon = TapeIcons.Chevron,
                                                size = 36.dp,
                                                onClick = {}
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
