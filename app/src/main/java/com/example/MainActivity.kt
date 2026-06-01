package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge to edge view drawing
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainLauncherWorkspace(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainLauncherWorkspace(viewModel: LauncherViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val apps by viewModel.appItems.collectAsStateWithLifecycle()
    val wallpaperIdx by viewModel.currentWallpaperIndex.collectAsStateWithLifecycle()
    val columnsPreference by viewModel.gridColumns.collectAsStateWithLifecycle()
    val activeApp by viewModel.activeAppOverlay.collectAsStateWithLifecycle()
    val timeStr by viewModel.timeString.collectAsStateWithLifecycle()
    val dateStr by viewModel.dateString.collectAsStateWithLifecycle()

    // Control center details
    val wifi by viewModel.wifiEnabled.collectAsStateWithLifecycle()
    val bluetooth by viewModel.bluetoothEnabled.collectAsStateWithLifecycle()
    val flashlight by viewModel.flashlightEnabled.collectAsStateWithLifecycle()
    val ccOpen by viewModel.controlCenterOpen.collectAsStateWithLifecycle()
    val brightness by viewModel.brightnessLevel.collectAsStateWithLifecycle()
    val volume by viewModel.volumeLevel.collectAsStateWithLifecycle()

    // Spotlight search toggles
    var searchOpen by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // System stats
    val battery by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val cpu by viewModel.cpuUsage.collectAsStateWithLifecycle()
    val ram by viewModel.ramUsage.collectAsStateWithLifecycle()

    // Theme wall selections
    val currentWallpaper = WallpaperEngine.wallpapers[wallpaperIdx]

    // Compose Native Pager: 3 screens: 1: Widgets, 2: App Screen, 3: App Library
    val pagerState = rememberPagerState(initialPage = 1) { 3 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentWallpaper.brush)
    ) {
        // Spotlight trigger (swipe down in the upper center)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            // Dragging down triggers spotlight
                            if (dragAmount.y > 22 && !ccOpen && !searchOpen) {
                                searchOpen = true
                            }
                        }
                    )
                }
        ) {
            // Main Scaffold containing Workspace elements
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    // Modern Floating Header Panel status bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (timeStr.isNotEmpty()) timeStr else "09:41",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Top Notch camera/capsule simulation
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(26.dp)
                                .background(Color.Black, RoundedCornerShape(13.dp))
                                .clickable {
                                    // Clicking the notch triggers Control Center!
                                    viewModel.toggleControlCenter()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(Color(0xFF00FFCC), CircleShape))
                                Text("NOTCH INTERACT", color = Color.White.copy(alpha = 0.5f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFFFF2D55), CircleShape))
                            }
                        }

                        // Status indicators
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Wifi, "WLAN logo", tint = Color.White, modifier = Modifier.size(13.dp))
                            Icon(Icons.Default.SignalCellularAlt, "Signal logo", tint = Color.White, modifier = Modifier.size(13.dp))
                            Text(
                                text = "${battery}%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Desktop page view area (Horizontal sliding pager)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { pageIndex ->
                                when (pageIndex) {
                                    // Page 1: Glass Widgets Section
                                    0 -> DesktopWidgetsPanel(viewModel, timeStr, dateStr, battery, cpu, ram, wifi, bluetooth, flashlight, volume, brightness)
                                    // Page 2: Custom Home App Grid
                                    1 -> DesktopAppsGrid(apps, columnsPreference, viewModel, onSearchClick = { searchOpen = true })
                                    // Page 3: Folder structured App Library
                                    2 -> AppLibraryPanel(apps, viewModel)
                                }
                            }
                        }

                        // Desktop Dot screen indicator
                        Row(
                            Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(pagerState.pageCount) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.35f)
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(7.dp)
                                )
                            }
                        }

                        // Permanent Glass Dock Bar at bottom
                        TranslucentHomeDock(apps, viewModel)
                    }
                }
            }
        }

        // FULL SCREEN OVERLAY: Control Center Slide Panel
        AnimatedVisibility(
            visible = ccOpen,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(250)
            ) + fadeOut()
        ) {
            ControlCenterOverlay(viewModel, wifi, bluetooth, flashlight, volume, brightness, battery, cpu, ram)
        }

        // FULL SCREEN OVERLAY: Spotlight search panel
        AnimatedVisibility(
            visible = searchOpen,
            enter = fadeIn(tween(150)) + scaleIn(initialScale = 1.05f),
            exit = fadeOut(tween(150)) + scaleOut(targetScale = 1.05f)
        ) {
            SpotlightSearchOverlay(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                apps = apps,
                viewModel = viewModel,
                onClose = {
                    viewModel.updateSearchQuery("")
                    searchOpen = false
                }
            )
        }

        // FULL SCREEN OVERLAY: Active simulated app display window
        AnimatedVisibility(
            visible = activeApp != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut()
        ) {
            activeApp?.let { app ->
                SimulatedAppContainer(
                    app = app,
                    viewModel = viewModel,
                    onClose = { viewModel.closeApp() }
                )
            }
        }
    }
}

// ---------------------- DESKTOP PAGES ----------------------

// PAGE 0: INTUITIVE GLASS WIDGETS PANEL
@Composable
fun DesktopWidgetsPanel(
    viewModel: LauncherViewModel,
    time: String,
    date: String,
    battery: Int,
    cpu: Int,
    ram: Float,
    wifi: Boolean,
    bluetooth: Boolean,
    flashlight: Boolean,
    volume: Float,
    brightness: Float
) {
    val activeWidgets by viewModel.activeWidgets.collectAsStateWithLifecycle()
    val playingState by viewModel.musicPlaying.collectAsStateWithLifecycle()
    val musicTrackIndex by viewModel.musicTrackIndex.collectAsStateWithLifecycle()
    val currentTrack = viewModel.soundtracks[musicTrackIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Smart Stack",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "iOS X Android Widgets",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = { viewModel.toggleControlCenter() },
                modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Tune, "Config", tint = Color.White)
            }
        }

        if (activeWidgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Keine Widgets aktiv.\nNutze die Launcher-Einstellungen,\num Widgets hinzuzufügen!",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            activeWidgets.forEach { widgetType ->
                when (widgetType) {
                    WidgetType.AESTHETIC_CLOCK -> {
                        AestheticClockWidget(time = time, date = date)
                    }
                    WidgetType.SYSTEM_BATTERY -> {
                        SystemEnergyWidget(battery = battery, cpu = cpu, ram = ram)
                    }
                    WidgetType.QUICK_SETTINGS -> {
                        SlateSystemControllers(
                            wifi = wifi,
                            bluetooth = bluetooth,
                            flashlight = flashlight,
                            onWifi = { viewModel.toggleWifi() },
                            onBluetooth = { viewModel.toggleBluetooth() },
                            onFlash = { viewModel.toggleFlashlight() },
                            volume = volume,
                            brightness = brightness,
                            onVolChange = { viewModel.adjustVolume(it) },
                            onBrightChange = { viewModel.adjustBrightness(it) }
                        )
                    }
                    WidgetType.WEATHER_CARD -> {
                        // Mini climate pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFFF9500), Color(0xFFFF5E3A))))
                                .clickable {
                                    // Open simulated weather app
                                    viewModel.openApp(AppItem("weather_app", "Wetter", null, IconType.WEATHER, "System Apps", Color(0xFF5AC8FA), true))
                                }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WbSunny, "Weather info", tint = Color.White, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("München", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Sonnig · Windstärken gering", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    }
                                }
                                Text("22°C", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    WidgetType.MUSIC_PLAYER -> {
                        MusicPlayerWidget(
                            viewModel = viewModel,
                            playingState = playingState,
                            currentTrack = currentTrack
                        )
                    }
                }
            }
        }
    }
}

// PAGE 1: HYBRID WORKSPACE APP GRID
@Composable
fun DesktopAppsGrid(
    apps: List<AppItem>,
    columnsPreference: Int,
    viewModel: LauncherViewModel,
    onSearchClick: () -> Unit
) {
    val gridRows by viewModel.gridRows.collectAsStateWithLifecycle()
    val verticalSpacedBy = when (gridRows) {
        3 -> 32.dp
        4 -> 24.dp
        5 -> 16.dp
        6 -> 8.dp
        else -> 16.dp
    }

    // Standard workspace grid consisting of core apps
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Themed Search Bar (Android-style in Elegant Dark)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .height(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(Color(0xFF27272A).copy(alpha = 0.6f)) // bg-zinc-800/60
                .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape) // border-white/10
                .clickable { onSearchClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = Color(0xFFA1A1AA), // zinc-400
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Nach Apps suchen...",
                        color = Color(0xFFA1A1AA), // zinc-400
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // AI assistant & secondary accent beads at the right slot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Assistant blue gradient circle from HTML mockup
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.8f), CircleShape)
                    )
                    // Secondary indicator
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                }
            }
        }

        val nonDockApps = apps.filter {
            // Keep specific core items out of main grid if we want, or display all
            it.id != "phone_app" && it.id != "safari_app" && it.id != "music_app" && it.id != "settings_app"
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsPreference),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(verticalSpacedBy)
        ) {
            items(nonDockApps) { app ->
                AppGridCard(app = app, viewModel = viewModel)
            }
        }
    }
}

// PAGE 2: APP LIBRARY CATEGORIZED PANEL
@Composable
fun AppLibraryPanel(
    apps: List<AppItem>,
    viewModel: LauncherViewModel
) {
    // Automatic folder lists based on product category fields
    val categoriesMap = apps.groupBy { it.category }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "App-Mediathek",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Automatisch sortiert",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categoriesMap.entries.forEach { entry ->
                item {
                    AppLibraryFolderCard(
                        folderName = entry.key,
                        folderApps = entry.value,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppLibraryFolderCard(
    folderName: String,
    folderApps: List<AppItem>,
    viewModel: LauncherViewModel
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        alpha = 0.12f
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = folderName.uppercase(),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            // Dynamic mini apps grid inside the folder (up to 4 icons)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                folderApps.take(4).forEach { app ->
                    val drawable = viewModel.cachedIcons[app.id]
                    Box(
                        modifier = Modifier
                            .clickable { viewModel.openApp(app) }
                    ) {
                        SquircleIcon(app = app, cachedDrawable = drawable, iconSize = 32)
                    }
                }
            }

            Text(
                text = "${folderApps.size} Apps",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------------- COMMON LAYOUT PARTS ----------------------

@Composable
fun AppGridCard(app: AppItem, viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val drawable = viewModel.cachedIcons[app.id]
    val iconSize by viewModel.iconSize.collectAsStateWithLifecycle()
    val iconPack by viewModel.currentIconPack.collectAsStateWithLifecycle()
    val activeTheme by viewModel.currentTheme.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .width((iconSize + 18).dp)
            .clickable {
                viewModel.openApp(app)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SquircleIcon(
            app = app,
            cachedDrawable = drawable,
            iconSize = iconSize.toInt(),
            iconPack = iconPack,
            activeTheme = activeTheme
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.name,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = TextStyle(
                shadow = Shadow(color = Color.Black.copy(alpha = 0.6f), offset = Offset(1f, 1f), blurRadius = 3f)
            )
        )
    }
}

// TRANSLUCENT HOME DOCK (Translucent pill overlay)
@Composable
fun TranslucentHomeDock(apps: List<AppItem>, viewModel: LauncherViewModel) {
    // Pinned core apps display at bottom base
    val dockApps = remember(apps) {
        listOf(
            apps.firstOrNull { it.id == "phone_app" } ?: AppItem("phone_app", "Phone", null, IconType.PHONE, "Dock", Color(0xFF34C759), true),
            apps.firstOrNull { it.id == "safari_app" } ?: AppItem("safari_app", "Safari", null, IconType.SAFARI, "Dock", Color(0xFF007AFF), true),
            apps.firstOrNull { it.id == "music_app" } ?: AppItem("music_app", "Music", null, IconType.MUSIC, "Dock", Color(0xFFFF2D55), true),
            apps.firstOrNull { it.id == "settings_app" } ?: AppItem("settings_app", "Settings", null, IconType.SETTINGS, "Dock", Color(0xFF5856D6), true)
        )
    }
    val iconSize by viewModel.iconSize.collectAsStateWithLifecycle()
    val iconPack by viewModel.currentIconPack.collectAsStateWithLifecycle()
    val activeTheme by viewModel.currentTheme.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(36.dp),
                clip = false
            )
            .clip(RoundedCornerShape(36.dp))
            .background(Color(0xFF1E1E20).copy(alpha = 0.45f)) // Beautiful dark translucent base
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(36.dp)) // border border-white/10
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dockApps.forEach { app ->
                val drawable = viewModel.cachedIcons[app.id]
                Column(
                    modifier = Modifier.clickable { viewModel.openApp(app) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SquircleIcon(
                        app = app,
                        cachedDrawable = drawable,
                        iconSize = (iconSize * 0.96f).toInt().coerceAtLeast(36),
                        iconPack = iconPack,
                        activeTheme = activeTheme
                    )
                }
            }
        }
    }
}

// CONTROL CENTER DROPDOWN OVERLAY PANEL
@Composable
fun ControlCenterOverlay(
    viewModel: LauncherViewModel,
    wifi: Boolean,
    bluetooth: Boolean,
    flashlight: Boolean,
    volume: Float,
    brightness: Float,
    battery: Int,
    cpu: Int,
    ram: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F10).copy(alpha = 0.82f))
            .blur(20.dp)
            .clickable(
                onClick = { viewModel.closeControlCenter() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .clickable(enabled = false) {}, // Consume taps within cc
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dismiss handle bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { viewModel.closeControlCenter() }
                )
            }

            Text("STATIONS CENTER", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Double Stack System controls and toggles
            SlateSystemControllers(
                wifi = wifi,
                bluetooth = bluetooth,
                flashlight = flashlight,
                onWifi = { viewModel.toggleWifi() },
                onBluetooth = { viewModel.toggleBluetooth() },
                onFlash = { viewModel.toggleFlashlight() },
                volume = volume,
                brightness = brightness,
                onVolChange = { viewModel.adjustVolume(it) },
                onBrightChange = { viewModel.adjustBrightness(it) }
            )

            // Equalizer Stats metrics indicators inside control panel
            SystemEnergyWidget(battery = battery, cpu = cpu, ram = ram)

            // Custom Media widget playback box
            val playingState = viewModel.musicPlaying.collectAsStateWithLifecycle().value
            val trackIdx = viewModel.musicTrackIndex.collectAsStateWithLifecycle().value
            val currentTrack = viewModel.soundtracks[trackIdx]

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFFF2D55), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(currentTrack.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(currentTrack.artist, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }

                    Row {
                        IconButton(onClick = { viewModel.playPrev() }) {
                            Icon(Icons.Default.SkipPrevious, "Back", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.toggleMusicPlay() }) {
                            Icon(
                                imageVector = if (playingState) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { viewModel.playNext() }) {
                            Icon(Icons.Default.SkipNext, "Next", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// SPOTLIGHT SEARCH OVERLAY
@Composable
fun SpotlightSearchOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    apps: List<AppItem>,
    viewModel: LauncherViewModel,
    onClose: () -> Unit
) {
    val matchedApps = remember(query, apps) {
        if (query.isEmpty()) emptyList()
        else apps.filter { it.name.contains(query, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable(
                onClick = onClose,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}, // consume close clicks
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input Block
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                placeholder = { Text("Apps und Launcher durchsuchen...", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, "Suche", tint = Color.White) },
                trailingIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Cancel, "Cancel", tint = Color.LightGray)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.12f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Results lists
            if (matchedApps.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(matchedApps) { app ->
                        val drawable = viewModel.cachedIcons[app.id]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.openApp(app)
                                    onClose()
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SquircleIcon(app = app, cachedDrawable = drawable, iconSize = 42)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(app.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(app.category, color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keine Apps oder Verknüpfungen gefunden", color = Color.LightGray, fontSize = 13.sp)
                }
            } else {
                // Quick shortcuts list
                Column {
                    Text("SCHNELLZUGRIFFE", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        apps.take(4).forEach { app ->
                            val drawable = viewModel.cachedIcons[app.id]
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                         viewModel.openApp(app)
                                         onClose()
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    SquircleIcon(app = app, cachedDrawable = drawable, iconSize = 32)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(app.name, color = Color.White, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
