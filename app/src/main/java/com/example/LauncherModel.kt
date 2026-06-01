package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

data class Wallpaper(
    val id: String,
    val name: String,
    val brush: Brush,
    val isDark: Boolean
)

enum class IconType {
    PHONE, SAFARI, CAMERA, MUSIC, CALENDAR, WEATHER, SETTINGS, CALCULATOR, ACTIVITY, NOTES, GENERIC
}

data class AppItem(
    val id: String,
    val name: String,
    val packageName: String? = null,
    val iconType: IconType = IconType.GENERIC,
    val category: String = "Utilities",
    val customColor: Color = Color.Gray,
    val isSimulated: Boolean = false
)

enum class WidgetType {
    AESTHETIC_CLOCK, WEATHER_CARD, QUICK_SETTINGS, SYSTEM_BATTERY, MUSIC_PLAYER
}

data class AppFolder(
    val name: String,
    val apps: List<AppItem>,
    val iconColor: Color
)
