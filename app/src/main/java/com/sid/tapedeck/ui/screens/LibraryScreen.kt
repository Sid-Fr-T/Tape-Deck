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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.sid.tapedeck.ui.components.SongRow
import com.sid.tapedeck.ui.components.TapeButton
import com.sid.tapedeck.ui.components.TapeCard
import com.sid.tapedeck.ui.components.TapeChip
import com.sid.tapedeck.ui.components.TapeIconButton
import com.sid.tapedeck.ui.components.TapeIcons
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.TapeType
import com.sid.tapedeck.ui.theme.chunkyBorder

private enum class LibraryTab(val label: String) {
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists"),
    Playlists("Playlists")
}

private enum class LibrarySort(val label: String) {
    ByName("By Name A-Z"),
    ModifiedDate("Modified Date"),
    AddDate("Add Date")
}

@Composable
fun LibraryScreen(
    libraryState: LibraryState,
    currentPlayingSong: Song?,
    isPlaying: Boolean,
    onPlaySong: (Song, List<Song>) -> Unit,
    onShuffleAll: (List<Song>) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val colors = TapeTheme.colors
    var selectedTab by remember { mutableStateOf(LibraryTab.Songs) }
    var currentSort by remember { mutableStateOf(LibrarySort.ByName) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val rawSongs = (libraryState as? LibraryState.Success)?.songs ?: emptyList()
    val albums = (libraryState as? LibraryState.Success)?.albums ?: emptyList()
    val artists = (libraryState as? LibraryState.Success)?.artists ?: emptyList()

    val sortedSongs = remember(rawSongs, currentSort) {
        when (currentSort) {
            LibrarySort.ByName -> rawSongs.sortedBy { it.title.lowercase() }
            LibrarySort.ModifiedDate -> rawSongs.sortedByDescending { it.dateModified }
            LibrarySort.AddDate -> rawSongs.sortedByDescending { it.dateAdded }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header "LIBRARY" + Search icon button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TapeText(
                text = "LIBRARY",
                style = TapeType.header,
                color = colors.ink
            )

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

        // Chips: Songs, Albums, Artists, Playlists
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LibraryTab.entries.forEach { tab ->
                TapeChip(
                    text = tab.label,
                    isSelected = selectedTab == tab,
                    onClick = { selectedTab = tab }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action row: Pink Play button, Yellow Shuffle button, Sort icon button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pink Play button
            TapeButton(
                text = "Play",
                leadingIcon = TapeIcons.Play,
                backgroundColor = colors.pink,
                textColor = colors.onPink,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (sortedSongs.isNotEmpty()) {
                        onPlaySong(sortedSongs.first(), sortedSongs)
                    }
                }
            )

            // Yellow Shuffle button with onAccent text
            TapeButton(
                text = "Shuffle",
                leadingIcon = TapeIcons.Shuffle,
                backgroundColor = colors.yellow,
                textColor = colors.onAccent,
                modifier = Modifier.weight(1.2f),
                onClick = {
                    if (sortedSongs.isNotEmpty()) {
                        onShuffleAll(sortedSongs)
                    }
                }
            )

            // Sort icon button (52dp rounded square)
            Box {
                TapeIconButton(
                    icon = TapeIcons.SortArrows,
                    size = 52.dp,
                    isSquare = true,
                    squareRadius = 16.dp,
                    backgroundColor = colors.card,
                    iconTint = colors.ink,
                    shadowDistance = 4.dp,
                    onClick = { sortMenuExpanded = true },
                    contentDescription = "Sort library"
                )

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    modifier = Modifier
                        .background(colors.card)
                        .chunkyBorder(colors.line, width = 3.dp, shape = RoundedCornerShape(12.dp))
                ) {
                    LibrarySort.entries.forEach { sortOption ->
                        DropdownMenuItem(
                            text = {
                                TapeText(
                                    text = sortOption.label,
                                    style = TapeType.rowTitle,
                                    color = if (currentSort == sortOption) colors.pinkText else colors.ink
                                )
                            },
                            onClick = {
                                currentSort = sortOption
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs Content
        when (selectedTab) {
            LibraryTab.Songs -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedSongs, key = { it.id }) { song ->
                        val isCurrentPlaying = isPlaying && currentPlayingSong?.id == song.id
                        SongRow(
                            title = song.title,
                            artist = song.artist,
                            duration = song.durationFormatted,
                            albumId = song.albumId.toString(),
                            artworkUri = song.albumArtUri,
                            isPlaying = isCurrentPlaying,
                            onClick = { onPlaySong(song, sortedSongs) }
                        )
                    }
                }
            }

            LibraryTab.Albums -> {
                // Albums tab: 2-column grid of TapeCards
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(albums, key = { it.id }) { album ->
                        TapeCard(
                            modifier = Modifier
                                .fillMaxWidth()
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
                                    size = 126.dp,
                                    modifier = Modifier.fillMaxWidth()
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

            LibraryTab.Artists -> {
                // Artists tab: simple rows
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(artists, key = { it.name }) { artist ->
                        TapeCard(
                            modifier = Modifier.fillMaxWidth(),
                            radius = 18.dp,
                            shadowOffset = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    TapeText(
                                        text = artist.name,
                                        style = TapeType.rowTitle,
                                        color = colors.ink,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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

            LibraryTab.Playlists -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        TapeCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (sortedSongs.isNotEmpty()) {
                                        onPlaySong(sortedSongs.first(), sortedSongs)
                                    }
                                },
                            radius = 18.dp,
                            shadowOffset = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(colors.pink, RoundedCornerShape(12.dp))
                                        .chunkyBorder(colors.line, 3.dp, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = TapeIcons.Heart,
                                        contentDescription = null,
                                        tint = colors.onPink
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    TapeText(
                                        text = "FAVORITE TAPES",
                                        style = TapeType.rowTitle,
                                        color = colors.ink
                                    )
                                    TapeText(
                                        text = "Quick playback of bookmarked tracks",
                                        style = TapeType.subtitle,
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
}
