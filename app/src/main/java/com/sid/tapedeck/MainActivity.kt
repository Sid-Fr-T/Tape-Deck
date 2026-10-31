package com.sid.tapedeck

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import com.sid.tapedeck.ui.components.TapeText
import com.sid.tapedeck.ui.theme.TapeType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sid.tapedeck.data.repository.LibraryState
import com.sid.tapedeck.player.PlayerViewModel
import com.sid.tapedeck.ui.components.BottomNav
import com.sid.tapedeck.ui.components.MiniPlayer
import com.sid.tapedeck.ui.screens.AlbumDetailScreen
import com.sid.tapedeck.ui.screens.HomeScreen
import com.sid.tapedeck.ui.screens.LibraryScreen
import com.sid.tapedeck.ui.screens.LyricsScreen
import com.sid.tapedeck.ui.screens.NowPlayingScreen
import com.sid.tapedeck.ui.screens.PermissionScreen
import com.sid.tapedeck.ui.screens.QueueScreen
import com.sid.tapedeck.ui.screens.SearchScreen
import com.sid.tapedeck.ui.screens.YouScreen
import com.sid.tapedeck.ui.theme.TapeTheme
import com.sid.tapedeck.ui.theme.ThemeMode
import com.sid.tapedeck.ui.theme.chunkyBorder
import com.sid.tapedeck.ui.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var selectedThemeMode by remember { mutableStateOf(ThemeMode.System) }

            TapeTheme(themeMode = selectedThemeMode) {
                val context = LocalContext.current
                val musicViewModel: MusicViewModel = viewModel()
                val playerViewModel: PlayerViewModel = viewModel()

                // Manage 2Hz position polling ONLY while UI is visible (zero background battery drain)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME -> {
                                playerViewModel.setUiVisible(true)
                            }
                            Lifecycle.Event.ON_STOP -> {
                                playerViewModel.setUiVisible(false)
                            }
                            else -> {}
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                        playerViewModel.setUiVisible(false)
                    }
                }

                val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }

                val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                var hasStoragePermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            storagePermission
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { resultMap ->
                    hasStoragePermission = resultMap[storagePermission] ?: false
                    if (hasStoragePermission) {
                        musicViewModel.scanLibrary()
                    }
                }

                if (!hasStoragePermission) {
                    PermissionScreen(
                        onRequestPermission = {
                            permissionLauncher.launch(permissionsToRequest)
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        musicViewModel.scanLibrary()
                    }

                    TapedeckAppShell(
                        musicViewModel = musicViewModel,
                        playerViewModel = playerViewModel,
                        currentTheme = selectedThemeMode,
                        onSelectTheme = { selectedThemeMode = it }
                    )
                }
            }
        }
    }
}

@Composable
fun TapedeckAppShell(
    musicViewModel: MusicViewModel,
    playerViewModel: PlayerViewModel,
    currentTheme: ThemeMode,
    onSelectTheme: (ThemeMode) -> Unit
) {
    val colors = TapeTheme.colors
    val typography = TapeTheme.typography
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    // MiniPlayer and BottomNav are docked only on the 4 main tabs
    val isMainTab = currentRoute in setOf("home", "library", "search", "you")

    val libraryState by musicViewModel.libraryState.collectAsState()
    val currentPlayingSong by playerViewModel.currentSong.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val currentPositionMs by playerViewModel.currentPositionMs.collectAsState()
    val durationMs by playerViewModel.durationMs.collectAsState()
    val shuffleModeEnabled by playerViewModel.shuffleModeEnabled.collectAsState()
    val repeatMode by playerViewModel.repeatMode.collectAsState()
    val queue by playerViewModel.queue.collectAsState()

    Scaffold(
        containerColor = colors.bg,
        bottomBar = {
            if (isMainTab) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Real Docked MiniPlayer directly above BottomNav
                    MiniPlayer(
                        title = currentPlayingSong?.title ?: "Select a cassette",
                        artist = currentPlayingSong?.artist ?: "Tapedeck Vault",
                        albumId = currentPlayingSong?.albumId?.toString(),
                        artworkUri = currentPlayingSong?.albumArtUri,
                        isPlaying = isPlaying,
                        onPlayPauseClick = { playerViewModel.playPause() },
                        onPreviousClick = { playerViewModel.previous() },
                        onNextClick = { playerViewModel.next() },
                        onClick = { navController.navigate("nowPlaying") }
                    )

                    // Docked BottomNav
                    BottomNav(
                        currentRoute = currentRoute,
                        onTabSelected = { targetRoute ->
                            if (currentRoute != targetRoute) {
                                navController.navigate(targetRoute) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Main 4 Tabs
            composable("home") {
                HomeScreen(
                    libraryState = libraryState,
                    currentPlayingSong = currentPlayingSong,
                    isPlaying = isPlaying,
                    onPlaySong = { selectedSong, playlist ->
                        val index = playlist.indexOfFirst { it.id == selectedSong.id }.coerceAtLeast(0)
                        playerViewModel.playList(playlist, index)
                    },
                    onShuffleAll = { allSongs ->
                        val shuffled = allSongs.shuffled()
                        playerViewModel.playList(shuffled, 0)
                    },
                    onRescan = {
                        musicViewModel.scanLibrary(force = true)
                    },
                    onNavigateToAlbum = { id -> navController.navigate("album/$id") },
                    onNavigateToSearch = { navController.navigate("search") },
                    onNavigateToLibrary = { navController.navigate("library") }
                )
            }
            composable("library") {
                LibraryScreen(
                    libraryState = libraryState,
                    currentPlayingSong = currentPlayingSong,
                    isPlaying = isPlaying,
                    onPlaySong = { selectedSong, playlist ->
                        val index = playlist.indexOfFirst { it.id == selectedSong.id }.coerceAtLeast(0)
                        playerViewModel.playList(playlist, index)
                    },
                    onShuffleAll = { allSongs ->
                        val shuffled = allSongs.shuffled()
                        playerViewModel.playList(shuffled, 0)
                    },
                    onNavigateToAlbum = { id -> navController.navigate("album/$id") },
                    onNavigateToSearch = { navController.navigate("search") }
                )
            }
            composable("search") {
                SearchScreen(
                    libraryState = libraryState,
                    currentPlayingSong = currentPlayingSong,
                    isPlaying = isPlaying,
                    onPlaySong = { selectedSong, playlist ->
                        val index = playlist.indexOfFirst { it.id == selectedSong.id }.coerceAtLeast(0)
                        playerViewModel.playList(playlist, index)
                    },
                    onNavigateToAlbum = { id -> navController.navigate("album/$id") }
                )
            }
            composable("you") {
                YouScreen(
                    currentTheme = currentTheme,
                    onSelectTheme = onSelectTheme
                )
            }

            // Player and Detail Screens (Hide BottomNav and MiniPlayer)
            composable("album/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: "tape"
                AlbumDetailScreen(
                    albumId = id,
                    onBack = { navController.popBackStack() },
                    onPlayTrack = { navController.navigate("nowPlaying") }
                )
            }
            composable("nowPlaying") {
                NowPlayingScreen(
                    song = currentPlayingSong,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    shuffleModeEnabled = shuffleModeEnabled,
                    repeatMode = repeatMode,
                    onTogglePlayPause = { playerViewModel.playPause() },
                    onNext = { playerViewModel.next() },
                    onPrevious = { playerViewModel.previous() },
                    onSeek = { playerViewModel.seekTo(it) },
                    onToggleShuffle = { playerViewModel.toggleShuffle() },
                    onCycleRepeat = { playerViewModel.cycleRepeatMode() },
                    onDismiss = { navController.popBackStack() },
                    onNavigateToQueue = { navController.navigate("queue") },
                    onNavigateToLyrics = { navController.navigate("lyrics") }
                )
            }
            composable("queue") {
                QueueScreen(
                    queue = queue,
                    currentSong = currentPlayingSong,
                    onPlayQueueIndex = { index ->
                        playerViewModel.playList(queue, index)
                    },
                    onRemoveFromQueue = { index ->
                        playerViewModel.removeFromQueue(index)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("lyrics") {
                LyricsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun ThemeToggleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = TapeTheme.colors
    val bg = if (isSelected) colors.pink else colors.card
    val textColor = if (isSelected) colors.onPink else colors.ink

    Box(
        modifier = Modifier
            .background(bg)
            .chunkyBorder(colors.line, width = 2.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        TapeText(
            text = label,
            style = TapeType.topBar,
            color = textColor
        )
    }
}
