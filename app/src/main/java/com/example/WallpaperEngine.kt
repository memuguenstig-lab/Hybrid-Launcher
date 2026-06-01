package com.example

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object WallpaperEngine {
    val wallpapers = listOf(
        Wallpaper(
            id = "elegant_dark",
            name = "Elegant Dark",
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF161618),
                    Color(0xFF0F0F10),
                    Color(0xFF0C0C0D)
                )
            ),
            isDark = true
        ),
        Wallpaper(
            id = "slate",
            name = "Aero Slate",
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF323B4E),
                    Color(0xFF1F2430),
                    Color(0xFF0F111A)
                ),
                radius = 1600f
            ),
            isDark = true
        ),
        Wallpaper(
            id = "sunset",
            name = "Sunset Flare",
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF7E5F),
                    Color(0xFFFEB47B),
                    Color(0xFF762B5F)
                ),
                radius = 1400f
            ),
            isDark = true
        ),
        Wallpaper(
            id = "aurora",
            name = "iOS Glass",
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF00F2FE),
                    Color(0xFF4FACFE),
                    Color(0xFF9B00E8),
                    Color(0xFFFF0844)
                ),
                start = Offset.Zero,
                end = Offset(1000f, 2000f)
            ),
            isDark = true
        ),
        Wallpaper(
            id = "emerald",
            name = "Android Mint",
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF11998E),
                    Color(0xFF38EF7D),
                    Color(0xFF0B4F35)
                ),
                start = Offset.Zero,
                end = Offset(1100f, 1500f)
            ),
            isDark = true
        ),
        Wallpaper(
            id = "dark_gold",
            name = "Classic Gold",
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1C1C24),
                    Color(0xFF2d242f),
                    Color(0xFF0A0A0D)
                ),
                start = Offset.Zero,
                end = Offset(800f, 1200f)
            ),
            isDark = true
        )
    )
}
