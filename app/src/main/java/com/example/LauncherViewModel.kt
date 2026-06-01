package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // Built-in beautifully styled simulated apps for immediate showcase
    private val simulatedApps = listOf(
        AppItem("phone_app", "Telefon", null, IconType.PHONE, "System Apps", Color(0xFF34C759), true),
        AppItem("safari_app", "Safari", null, IconType.SAFARI, "Creativity", Color(0xFF007AFF), true),
        AppItem("camera_app", "Kamera", null, IconType.CAMERA, "Utilities", Color(0xFF8E8E93), true),
        AppItem("music_app", "Musik", null, IconType.MUSIC, "Media", Color(0xFFFF2D55), true),
        AppItem("weather_app", "Wetter", null, IconType.WEATHER, "System Apps", Color(0xFF5AC8FA), true),
        AppItem("settings_app", "Launcher", null, IconType.SETTINGS, "System Apps", Color(0xFF5856D6), true),
        AppItem("calculator_app", "Rechner", null, IconType.CALCULATOR, "Utilities", Color(0xFFFF9500), true),
        AppItem("notes_app", "Notizen", null, IconType.NOTES, "Creativity", Color(0xFFFFCC00), true),
        AppItem("activity_app", "Fitness", null, IconType.ACTIVITY, "Media", Color(0xFF00D3FF), true)
    )

    // All available apps (simulated + real system apps compiled together)
    private val _appItems = MutableStateFlow<List<AppItem>>(simulatedApps)
    val appItems: StateFlow<List<AppItem>> = _appItems.asStateFlow()

    // Loaded package icons for real apps indexed by package ID to keep rendering highly fast
    val cachedIcons = mutableMapOf<String, Drawable>()

    // Selected wallpaper selection
    private val _currentWallpaperIndex = MutableStateFlow(0)
    val currentWallpaperIndex: StateFlow<Int> = _currentWallpaperIndex.asStateFlow()

    // Page preferences: columns setting
    private val _gridColumns = MutableStateFlow(4)
    val gridColumns: StateFlow<Int> = _gridColumns.asStateFlow()

    // Page preferences: rows setting
    private val _gridRows = MutableStateFlow(5)
    val gridRows: StateFlow<Int> = _gridRows.asStateFlow()

    // App icon size preference in dp
    private val _iconSize = MutableStateFlow(56f)
    val iconSize: StateFlow<Float> = _iconSize.asStateFlow()

    // Active widgets state on home screen
    private val _activeWidgets = MutableStateFlow<List<WidgetType>>(listOf(
        WidgetType.AESTHETIC_CLOCK,
        WidgetType.SYSTEM_BATTERY,
        WidgetType.QUICK_SETTINGS,
        WidgetType.WEATHER_CARD
    ))
    val activeWidgets: StateFlow<List<WidgetType>> = _activeWidgets.asStateFlow()

    // Theme engine states
    private val _currentTheme = MutableStateFlow("Liquid Glass Glow")
    val currentTheme: StateFlow<String> = _currentTheme.asStateFlow()

    private val _accentColorHex = MutableStateFlow("#007AFF")
    val accentColorHex: StateFlow<String> = _accentColorHex.asStateFlow()

    private val _currentIconPack = MutableStateFlow("Standard Pack")
    val currentIconPack: StateFlow<String> = _currentIconPack.asStateFlow()

    // Downloaded icon packs simulation
    private val _downloadedIconPacks = MutableStateFlow<List<String>>(listOf("Standard Pack", "Comic Vector Pack"))
    val downloadedIconPacks: StateFlow<List<String>> = _downloadedIconPacks.asStateFlow()

    // Active screen navigation
    private val _activeAppOverlay = MutableStateFlow<AppItem?>(null)
    val activeAppOverlay: StateFlow<AppItem?> = _activeAppOverlay.asStateFlow()

    // Clock strings
    private val _timeString = MutableStateFlow("")
    val timeString: StateFlow<String> = _timeString.asStateFlow()

    private val _dateString = MutableStateFlow("")
    val dateString: StateFlow<String> = _dateString.asStateFlow()

    // Status / Settings toggles
    private val _wifiEnabled = MutableStateFlow(true)
    val wifiEnabled: StateFlow<Boolean> = _wifiEnabled.asStateFlow()

    private val _bluetoothEnabled = MutableStateFlow(true)
    val bluetoothEnabled: StateFlow<Boolean> = _bluetoothEnabled.asStateFlow()

    private val _flashlightEnabled = MutableStateFlow(false)
    val flashlightEnabled: StateFlow<Boolean> = _flashlightEnabled.asStateFlow()

    private val _controlCenterOpen = MutableStateFlow(false)
    val controlCenterOpen: StateFlow<Boolean> = _controlCenterOpen.asStateFlow()

    // Slider settings
    private val _brightnessLevel = MutableStateFlow(0.8f)
    val brightnessLevel: StateFlow<Float> = _brightnessLevel.asStateFlow()

    private val _volumeLevel = MutableStateFlow(0.6f)
    val volumeLevel: StateFlow<Float> = _volumeLevel.asStateFlow()

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // System stats: real battery and dynamic computer resource loops
    private val _batteryLevel = MutableStateFlow(84)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _ramUsage = MutableStateFlow(4.2f)
    val ramUsage: StateFlow<Float> = _ramUsage.asStateFlow()

    private val _cpuUsage = MutableStateFlow(12)
    val cpuUsage: StateFlow<Int> = _cpuUsage.asStateFlow()

    // Music Player inside control panel/app
    private val _musicPlaying = MutableStateFlow(false)
    val musicPlaying: StateFlow<Boolean> = _musicPlaying.asStateFlow()

    private val _musicTrackIndex = MutableStateFlow(0)
    val musicTrackIndex: StateFlow<Int> = _musicTrackIndex.asStateFlow()

    val soundtracks = listOf(
        Soundtrack("Lofi Breeze", "Purrple Cat", "03:12"),
        Soundtrack("Cyberpunk Neon", "Midnight Riders", "02:45"),
        Soundtrack("Summer Glide", "Ocean Breeze", "04:02"),
        Soundtrack("Quiet Focus", "Brain Wave", "05:15")
    )

    data class Soundtrack(val title: String, val artist: String, val duration: String)

    private var clockJob: Job? = null
    private var statsJob: Job? = null

    init {
        startWorkingLoops()
        loadInstalledLauncherApps()
    }

    private fun startWorkingLoops() {
        // Clock loop
        clockJob = viewModelScope.launch {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, d. MMMM", Locale.GERMAN)
            while (true) {
                val cal = Calendar.getInstance()
                _timeString.value = timeFormat.format(cal.time)
                _dateString.value = dateFormat.format(cal.time)
                delay(1000)
            }
        }

        // Stats fluctuator
        statsJob = viewModelScope.launch {
            while (true) {
                delay(2000)
                _cpuUsage.value = Random.nextInt(4, 38)
                _ramUsage.value = (3.8f + Random.nextFloat() * 1.5f).let { Math.round(it * 10f) / 10f }
            }
        }
    }

    fun loadInstalledLauncherApps() {
        viewModelScope.launch {
            try {
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val launchableList = pm.queryIntentActivities(intent, 0)
                val newList = simulatedApps.toMutableList()

                for (info in launchableList) {
                    val pkgName = info.activityInfo.packageName
                    // Skip launcher's own package to avoid recursive launch loop inside dock!
                    if (pkgName == context.packageName) continue

                    val appLabel = info.loadLabel(pm).toString()
                    val id = "real_$pkgName"

                    // Cache drawable
                    try {
                        val iconDrawable = info.loadIcon(pm)
                        cachedIcons[id] = iconDrawable
                    } catch (e: Exception) {
                        // ignore error loading individual drawables
                    }

                    // Simple category resolver
                    val category = when {
                        pkgName.contains("social") || pkgName.contains("whatsapp") || pkgName.contains("chat") || pkgName.contains("messenger") || pkgName.contains("facebook") || pkgName.contains("instagram") || pkgName.contains("twitter") || pkgName.contains("tele") -> "Social"
                        pkgName.contains("video") || pkgName.contains("music") || pkgName.contains("spotify") || pkgName.contains("youtube") || pkgName.contains("netflix") || pkgName.contains("game") || pkgName.contains("play") -> "Media"
                        pkgName.contains("tool") || pkgName.contains("setting") || pkgName.contains("camera") || pkgName.contains("file") || pkgName.contains("calculat") || pkgName.contains("clock") || pkgName.contains("gallery") -> "Utilities"
                        else -> "System Apps"
                    }

                    val customAccentColor = when (category) {
                        "Social" -> Color(0xFF007AFF)
                        "Media" -> Color(0xFFFF2D55)
                        "Utilities" -> Color(0xFFFF9500)
                        else -> Color(0xFF34C759)
                    }

                    // Only add if not duplicates
                    if (newList.none { it.packageName == pkgName }) {
                        newList.add(
                            AppItem(
                                id = id,
                                name = appLabel,
                                packageName = pkgName,
                                iconType = IconType.GENERIC,
                                category = category,
                                customColor = customAccentColor,
                                isSimulated = false
                            )
                        )
                    }
                }
                _appItems.value = newList
            } catch (e: Exception) {
                // Keep simulated apps on error loading package manager lists
            }
        }
    }

    fun selectWallpaper(idx: Int) {
        _currentWallpaperIndex.value = idx
    }

    fun setGridColumns(cols: Int) {
        if (cols in 3..6) {
            _gridColumns.value = cols
        }
    }

    fun setGridRows(rows: Int) {
        if (rows in 3..6) {
            _gridRows.value = rows
        }
    }

    fun setIconSize(size: Float) {
        _iconSize.value = size.coerceIn(40f, 75f)
    }

    fun selectTheme(theme: String) {
        _currentTheme.value = theme
    }

    fun selectAccentColor(hex: String) {
        _accentColorHex.value = hex
    }

    fun selectIconPack(pack: String) {
        _currentIconPack.value = pack
    }

    fun addDownloadedPack(pack: String) {
        if (!_downloadedIconPacks.value.contains(pack)) {
            _downloadedIconPacks.value = _downloadedIconPacks.value + pack
        }
    }

    fun addWidget(type: WidgetType) {
        if (!_activeWidgets.value.contains(type)) {
            _activeWidgets.value = _activeWidgets.value + type
        }
    }

    fun removeWidget(type: WidgetType) {
        _activeWidgets.value = _activeWidgets.value - type
    }

    fun repositionWidgetUp(type: WidgetType) {
        val current = _activeWidgets.value.toMutableList()
        val index = current.indexOf(type)
        if (index > 0) {
            current.removeAt(index)
            current.add(index - 1, type)
            _activeWidgets.value = current
        }
    }

    fun repositionWidgetDown(type: WidgetType) {
        val current = _activeWidgets.value.toMutableList()
        val index = current.indexOf(type)
        if (index >= 0 && index < current.size - 1) {
            current.removeAt(index)
            current.add(index + 1, type)
            _activeWidgets.value = current
        }
    }

    fun openApp(app: AppItem) {
        if (app.isSimulated) {
            _activeAppOverlay.value = app
        } else {
            // Actual launch
            app.packageName?.let { pkg ->
                try {
                    val pm = context.packageManager
                    val launchIntent = pm.getLaunchIntentForPackage(pkg)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    }
                } catch (e: Exception) {
                    // fall back to mock window launch on failure
                    _activeAppOverlay.value = app.copy(isSimulated = true)
                }
            }
        }
    }

    fun closeApp() {
        _activeAppOverlay.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Toggle control settings
    fun toggleWifi() { _wifiEnabled.value = !_wifiEnabled.value }
    fun toggleBluetooth() { _bluetoothEnabled.value = !_bluetoothEnabled.value }
    fun toggleFlashlight() { _flashlightEnabled.value = !_flashlightEnabled.value }
    fun toggleControlCenter() { _controlCenterOpen.value = !_controlCenterOpen.value }
    fun closeControlCenter() { _controlCenterOpen.value = false }

    fun adjustBrightness(level: Float) { _brightnessLevel.value = level.coerceIn(0.1f, 1f) }
    fun adjustVolume(level: Float) { _volumeLevel.value = level.coerceIn(0f, 1f) }

    // Music control center triggers
    fun toggleMusicPlay() { _musicPlaying.value = !_musicPlaying.value }
    fun playNext() {
        _musicTrackIndex.value = (_musicTrackIndex.value + 1) % soundtracks.size
    }
    fun playPrev() {
        _musicTrackIndex.value = if (_musicTrackIndex.value == 0) soundtracks.size - 1 else _musicTrackIndex.value - 1
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel()
        statsJob?.cancel()
    }
}
