package com.example

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

fun Color.lighten(factor: Float = 0.15f): Color {
    return Color(
        red = (red + (1f - red) * factor).coerceIn(0f, 1f),
        green = (green + (1f - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1f - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}

fun Color.darken(factor: Float = 0.18f): Color {
    return Color(
        red = (red * (1f - factor)).coerceIn(0f, 1f),
        green = (green * (1f - factor)).coerceIn(0f, 1f),
        blue = (blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

data class CartoonIconSpec(
    val glyph: ImageVector,
    val backgroundColor: Color,
    val isGenericFallback: Boolean = false
)

fun getCartoonIconSpec(app: AppItem): CartoonIconSpec {
    val nameLower = app.name.lowercase()
    val pkgLower = app.packageName?.lowercase() ?: ""

    return when {
        // PHONE / TELEFON
        app.iconType == IconType.PHONE ||
        nameLower.contains("phone") || nameLower.contains("telefon") || nameLower.contains("dialer") || nameLower.contains("anruf") ||
        pkgLower.contains("dialer") || pkgLower.contains("phone") -> {
            CartoonIconSpec(Icons.Default.Call, Color(0xFF34C759))
        }

        // SAFARI / CHROME / BROWSER
        app.iconType == IconType.SAFARI ||
        nameLower.contains("safari") || nameLower.contains("chrome") || nameLower.contains("browser") || nameLower.contains("internet") || nameLower.contains("web") || nameLower.contains("firefox") || nameLower.contains("opera") ||
        pkgLower.contains("browser") || pkgLower.contains("chrome") -> {
            CartoonIconSpec(Icons.Default.Explore, Color(0xFF007AFF))
        }

        // CAMERA / PHOTO / GALLERY
        app.iconType == IconType.CAMERA ||
        nameLower.contains("kamera") || nameLower.contains("camera") || nameLower.contains("fotos") || nameLower.contains("galerie") || nameLower.contains("gallery") || nameLower.contains("photo") || nameLower.contains("bild") ||
        pkgLower.contains("camera") || pkgLower.contains("gallery") || pkgLower.contains("photos") -> {
            CartoonIconSpec(Icons.Default.PhotoCamera, Color(0xFF8E8E93))
        }

        // MUSIC / SPOTIFY
        app.iconType == IconType.MUSIC ||
        nameLower.contains("music") || nameLower.contains("musik") || nameLower.contains("spotify") || nameLower.contains("sound") || nameLower.contains("audio") || nameLower.contains("radio") ||
        pkgLower.contains("music") || pkgLower.contains("spotify") || pkgLower.contains("audio") -> {
            CartoonIconSpec(Icons.Default.MusicNote, Color(0xFFFF2D55))
        }

        // WEATHER
        app.iconType == IconType.WEATHER ||
        nameLower.contains("wetter") || nameLower.contains("weather") || nameLower.contains("clima") || nameLower.contains("tempo") ||
        pkgLower.contains("weather") -> {
            CartoonIconSpec(Icons.Default.Cloud, Color(0xFF5AC8FA))
        }

        // CALCULATOR
        app.iconType == IconType.CALCULATOR ||
        nameLower.contains("calculator") || nameLower.contains("rechner") || nameLower.contains("taschenrechner") || nameLower.contains("calc") ||
        pkgLower.contains("calculat") -> {
            CartoonIconSpec(Icons.Default.Calculate, Color(0xFFFF9500))
        }

        // NOTES
        app.iconType == IconType.NOTES ||
        nameLower.contains("notes") || nameLower.contains("notiz") || nameLower.contains("keep") || nameLower.contains("memo") || nameLower.contains("text") || nameLower.contains("write") ||
        pkgLower.contains("note") -> {
            CartoonIconSpec(Icons.Default.StickyNote2, Color(0xFFFFCC00))
        }

        // FITNESS / HEALTH
        app.iconType == IconType.ACTIVITY ||
        nameLower.contains("activity") || nameLower.contains("fitness") || nameLower.contains("sport") || nameLower.contains("health") || nameLower.contains("gesund") || nameLower.contains("run") ||
        pkgLower.contains("fitness") || pkgLower.contains("health") -> {
            CartoonIconSpec(Icons.Default.DirectionsRun, Color(0xFF00D3FF))
        }

        // SETTINGS / LAUNCHER
        app.iconType == IconType.SETTINGS ||
        nameLower.contains("settings") || nameLower.contains("einstell") || nameLower.contains("setup") || nameLower.contains("config") || nameLower.contains("launcher") ||
        pkgLower.contains("settings") || pkgLower.contains("launcher") -> {
            CartoonIconSpec(Icons.Default.Settings, Color(0xFF5856D6))
        }

        // WHATSAPP / SOCIAL MESSENGER / CHAT
        nameLower.contains("whatsapp") || nameLower.contains("chat") || nameLower.contains("message") || nameLower.contains("nachricht") || nameLower.contains("sms") || nameLower.contains("telegram") || nameLower.contains("signal") || nameLower.contains("messenger") ||
        pkgLower.contains("whatsapp") || pkgLower.contains("message") || pkgLower.contains("chat") || pkgLower.contains("sms") || pkgLower.contains("tele") -> {
            CartoonIconSpec(Icons.Default.Sms, Color(0xFF25D366))
        }

        // YOUTUBE / VIDEO
        nameLower.contains("youtube") || nameLower.contains("video") || nameLower.contains("stream") || nameLower.contains("netflix") ||
        pkgLower.contains("youtube") || pkgLower.contains("video") -> {
            CartoonIconSpec(Icons.Default.PlayArrow, Color(0xFFFF3B30))
        }

        // PLAY STORE
        nameLower.contains("store") || nameLower.contains("laden") || nameLower.contains("shop") || pkgLower.contains("vending") -> {
            CartoonIconSpec(Icons.Default.ShoppingCart, Color(0xFF0A84FF))
        }

        // FILES / DIRECTORIES
        nameLower.contains("file") || nameLower.contains("datei") || nameLower.contains("ordner") || nameLower.contains("explorer") ||
        pkgLower.contains("file") || pkgLower.contains("document") -> {
            CartoonIconSpec(Icons.Default.Folder, Color(0xFFFF9500))
        }

        // EMAIL
        nameLower.contains("mail") || nameLower.contains("email") || nameLower.contains("gmail") || nameLower.contains("outlook") ||
        pkgLower.contains("mail") || pkgLower.contains("gmail") -> {
            CartoonIconSpec(Icons.Default.Email, Color(0xFFFF2D55))
        }

        // MAPS
        nameLower.contains("map") || nameLower.contains("karte") || nameLower.contains("navigation") ||
        pkgLower.contains("map") || pkgLower.contains("navigation") -> {
            CartoonIconSpec(Icons.Default.LocationOn, Color(0xFF34C759))
        }

        // CONTACTS / SOCIAL
        nameLower.contains("contact") || nameLower.contains("kontakt") || nameLower.contains("people") || nameLower.contains("person") || nameLower.contains("facebook") || nameLower.contains("instagram") -> {
            CartoonIconSpec(Icons.Default.AccountCircle, Color(0xFF5856D6))
        }

        else -> {
            val charCode = app.name.firstOrNull()?.uppercaseChar()?.code ?: 65
            val colors = listOf(
                Color(0xFF34C759), Color(0xFF007AFF), Color(0xFFFF2D55), Color(0xFFFF9500), Color(0xFF5856D6),
                Color(0xFF5AC8FA), Color(0xFFFFCC00), Color(0xFF00D3FF)
            )
            val computedColor = colors[charCode % colors.size]
            CartoonIconSpec(Icons.Default.Apps, app.customColor.takeIf { it != Color.Gray } ?: computedColor, isGenericFallback = true)
        }
    }
}

@Composable
fun CartoonVectorGlyph(
    icon: ImageVector,
    iconSize: Float,
    glyphColor: Color = Color.White,
    borderColor: Color = Color(0xFF101115),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val outlineOffset = 1.3f
        val sizeDp = (iconSize * 0.48).dp

        listOf(
            Offset(-outlineOffset, -outlineOffset),
            Offset(outlineOffset, -outlineOffset),
            Offset(-outlineOffset, outlineOffset),
            Offset(outlineOffset, outlineOffset),
            Offset(0f, -outlineOffset),
            Offset(0f, outlineOffset),
            Offset(-outlineOffset, 0f),
            Offset(outlineOffset, 0f)
        ).forEach { offset ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier
                    .size(sizeDp)
                    .offset(x = offset.x.dp, y = offset.y.dp)
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = glyphColor,
            modifier = Modifier.size(sizeDp)
        )
    }
}

// Custom Squircle icon container which resolves real and simulated app icons
@Composable
fun SquircleIcon(
    app: AppItem,
    cachedDrawable: Drawable?,
    modifier: Modifier = Modifier,
    iconSize: Int = 56,
    iconPack: String = "Standard Pack",
    activeTheme: String = "Liquid Glass Glow"
) {
    val roundedRadius = (iconSize * 0.24).dp
    val shape = RoundedCornerShape(roundedRadius)

    when (iconPack) {
        "Comic Vector Pack", "Classic Pack" -> {
            val spec = getCartoonIconSpec(app)
            val comicShape = RoundedCornerShape((iconSize * 0.24).dp)

            Box(
                modifier = modifier.size(iconSize.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. Dark Cartoon physical 2D drop shadow offset
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = 3.dp)
                        .background(Color(0xFF101115), comicShape)
                )

                // 2. Main glossy cartoon tile
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    spec.backgroundColor.lighten(0.18f),
                                    spec.backgroundColor
                                )
                            ),
                            comicShape
                        )
                        .border(2.5.dp, Color(0xFF101115), comicShape)
                        .drawBehind {
                            // High gloss bubble dome reflection on top
                            drawRect(
                                color = Color.White.copy(alpha = 0.22f),
                                size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.38f)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (spec.isGenericFallback && cachedDrawable != null) {
                        Box(
                            modifier = Modifier
                                .size((iconSize * 0.52).dp)
                                .background(Color.White, CircleShape)
                                .border(2.dp, Color(0xFF101115), CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    ImageView(ctx).apply {
                                        scaleType = ImageView.ScaleType.FIT_CENTER
                                        setImageDrawable(cachedDrawable)
                                    }
                                },
                                update = { view ->
                                    view.setImageDrawable(cachedDrawable)
                                }
                            )
                        }
                    } else {
                        CartoonVectorGlyph(
                            icon = spec.glyph,
                            iconSize = iconSize.toFloat(),
                            glyphColor = Color.White
                        )
                    }
                }
            }
        }

        "Unicons Line Pack" -> {
            // Pristine white minimalist vector line tile, matching the uploaded Unicons image!
            Box(
                modifier = modifier
                    .size(iconSize.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = shape,
                        clip = false,
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Main pure white card tile with fine border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, shape)
                        .border(1.2.dp, Color(0xFFE2E8F0), shape)
                        .padding(if (app.isSimulated) 0.dp else 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (app.isSimulated) {
                        val m3Icon = when (app.iconType) {
                            IconType.PHONE -> Icons.Default.Call
                            IconType.SAFARI -> Icons.Default.Explore
                            IconType.CAMERA -> Icons.Default.PhotoCamera
                            IconType.MUSIC -> Icons.Default.MusicNote
                            IconType.CALENDAR -> Icons.Default.CalendarToday
                            IconType.WEATHER -> Icons.Default.Cloud
                            IconType.SETTINGS -> Icons.Default.Settings
                            IconType.CALCULATOR -> Icons.Default.Calculate
                            IconType.ACTIVITY -> Icons.Default.DirectionsRun
                            IconType.NOTES -> Icons.Default.StickyNote2
                            else -> Icons.Default.Apps
                        }

                        Icon(
                            imageVector = m3Icon,
                            contentDescription = app.name,
                            tint = Color(0xFF1E222E), // Precise elegant dark graphite color from Unicons art
                            modifier = Modifier.size((iconSize * 0.44).dp)
                        )
                    } else {
                        // For real apps, display inside a nice clean bounded tile
                        if (cachedDrawable != null) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    ImageView(ctx).apply {
                                        scaleType = ImageView.ScaleType.FIT_CENTER
                                        setImageDrawable(cachedDrawable)
                                    }
                                },
                                update = { view ->
                                    view.setImageDrawable(cachedDrawable)
                                }
                            )
                        } else {
                            // High-quality monogram/letter standard
                            Text(
                                text = app.name.take(1).uppercase(),
                                color = Color(0xFF1E222E),
                                fontSize = (iconSize * 0.38).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        else -> { // "Standard Pack" (default system launcher style)
            val baseModifier = modifier
                .size(iconSize.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = shape,
                    clip = false
                )
                .clip(shape)

            if (app.isSimulated) {
                Box(
                    modifier = baseModifier.background(
                        Brush.verticalGradient(
                            colors = listOf(
                                app.customColor.lighten(0.12f),
                                app.customColor
                            )
                        )
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    val m3Icon = when (app.iconType) {
                        IconType.PHONE -> Icons.Default.Call
                        IconType.SAFARI -> Icons.Default.Explore
                        IconType.CAMERA -> Icons.Default.PhotoCamera
                        IconType.MUSIC -> Icons.Default.MusicNote
                        IconType.CALENDAR -> Icons.Default.CalendarToday
                        IconType.WEATHER -> Icons.Default.Cloud
                        IconType.SETTINGS -> Icons.Default.Settings
                        IconType.CALCULATOR -> Icons.Default.Calculate
                        IconType.ACTIVITY -> Icons.Default.DirectionsRun
                        IconType.NOTES -> Icons.Default.StickyNote2
                        else -> Icons.Default.Apps
                    }

                    Icon(
                        imageVector = m3Icon,
                        contentDescription = app.name,
                        tint = Color.White,
                        modifier = Modifier.size((iconSize * 0.48).dp)
                    )
                }
            } else {
                Box(
                    modifier = modifier.size(iconSize.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (cachedDrawable != null) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                ImageView(ctx).apply {
                                    scaleType = ImageView.ScaleType.FIT_CENTER
                                    setImageDrawable(cachedDrawable)
                                }
                            },
                            update = { view ->
                                view.setImageDrawable(cachedDrawable)
                            }
                        )
                    } else {
                        // High quality fallback circle monogram
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(app.customColor, app.customColor.copy(alpha = 0.65f))
                                    ),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.name.take(1).uppercase(),
                                color = Color.White,
                                fontSize = (iconSize * 0.45).sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// 1. WIDGET: AESTHETIC CLOCK & CALENDAR CARD (iOS style)
@Composable
fun AestheticClockWidget(time: String, date: String) {
    // Elegant Sky Blue Material You / iOS Hybrid Large Hero Widget
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(174.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                clip = false
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFFBAE6FD)) // sky-200 base
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Widget Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = date.substringBefore(",").uppercase(),
                        color = Color(0xFF082F49).copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = date.substringAfter(", ").ifEmpty { "Kalender" },
                        color = Color(0xFF082F49),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 28.sp
                    )
                }

                // Clock Badge (Glassmorphic look inside sky-200)
                Box(
                    modifier = Modifier
                        .size(width = 90.dp, height = 44.dp)
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "UTC CLOCK",
                            color = Color(0xFF082F49).copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = time,
                            color = Color(0xFF082F49),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Temperature / Weather Bottom bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "22°",
                        color = Color(0xFF082F49),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "Überwiegend Sonnig",
                        color = Color(0xFF082F49).copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Three overlapping styled indicator circles from the theme HTML
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-10).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(2.dp, Color(0xFFBAE6FD), CircleShape)
                            .background(Color(0xFF075985), CircleShape) // sky-800
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(2.dp, Color(0xFFBAE6FD), CircleShape)
                            .background(Color(0xFF0284C7), CircleShape) // sky-600
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .border(2.dp, Color(0xFFBAE6FD), CircleShape)
                            .background(Color(0xFF38BDF8), CircleShape) // sky-400
                    )
                }
            }
        }
    }
}

// 2. WIDGET: SYSTEM ENERGY RINGS (Battery and RAM)
@Composable
fun SystemEnergyWidget(battery: Int, cpu: Int, ram: Float) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ring 1: Battery
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Circle track
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(Color.White.copy(alpha = 0.1f), style = androidx.compose.ui.graphics.drawscope.Stroke(7.dp.toPx()))
                        drawArc(
                            color = Color(0xFF34C759),
                            startAngle = -90f,
                            sweepAngle = battery * 3.6f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(7.dp.toPx())
                        )
                    }
                    Text("${battery}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Akku", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Ring 2: CPU loads tracker
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(Color.White.copy(alpha = 0.1f), style = androidx.compose.ui.graphics.drawscope.Stroke(7.dp.toPx()))
                        drawArc(
                            color = Color(0xFFFF9500),
                            startAngle = -90f,
                            sweepAngle = cpu * 3.6f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(7.dp.toPx())
                        )
                    }
                    Text("${cpu}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("CPU", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Ring 3: RAM
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val ramPercentage = (ram / 8f) * 100f
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(Color.White.copy(alpha = 0.1f), style = androidx.compose.ui.graphics.drawscope.Stroke(7.dp.toPx()))
                        drawArc(
                            color = Color(0xFF007AFF),
                            startAngle = -90f,
                            sweepAngle = ramPercentage * 3.6f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(7.dp.toPx())
                        )
                    }
                    Text("${ram}G", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("RAM", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 3. WIDGET: SLATE CONTROLS SYSTEM
@Composable
fun SlateSystemControllers(
    wifi: Boolean,
    bluetooth: Boolean,
    flashlight: Boolean,
    onWifi: () -> Unit,
    onBluetooth: () -> Unit,
    onFlash: () -> Unit,
    volume: Float,
    brightness: Float,
    onVolChange: (Float) -> Unit,
    onBrightChange: (Float) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SystemToggleIcon(icon = Icons.Default.Wifi, active = wifi, onClick = onWifi, label = "WLAN")
                SystemToggleIcon(icon = Icons.Default.Bluetooth, active = bluetooth, onClick = onBluetooth, label = "BT")
                SystemToggleIcon(icon = Icons.Default.FlashlightOn, active = flashlight, onClick = onFlash, label = "Blitz")
            }

            // Sliders mini Row
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Brightness
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.WbSunny, "Helligkeit", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = brightness,
                        onValueChange = onBrightChange,
                        modifier = Modifier.weight(1f).height(10.dp),
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f),
                            thumbColor = Color.White
                        )
                    )
                }

                // Volume
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VolumeUp, "Lautstärke", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = volume,
                        onValueChange = onVolChange,
                        modifier = Modifier.weight(1f).height(10.dp),
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f),
                            thumbColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SystemToggleIcon(
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    if (active) Color(0xFF007AFF) else Color.White.copy(alpha = 0.1f),
                    CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (active) Color.White else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// 4. WIDGET: MUSIC PLAYER COMPONENT
@Composable
fun MusicPlayerWidget(
    viewModel: LauncherViewModel,
    playingState: Boolean,
    currentTrack: LauncherViewModel.Soundtrack
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vinyl / Album art simulation
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(Color(0xFFFF2D55), Color(0xFFFF9500), Color(0xFF007AFF), Color(0xFFFF2D55))
                            ),
                            CircleShape
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Center hole
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color(0xFF0C0E14), CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = currentTrack.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentTrack.artist,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Simple animated voice/sound waves if playing, otherwise flat dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(14.dp)
                    ) {
                        for (i in 1..8) {
                            val duration = 400 + i * 80
                            val heightFactor by rememberInfiniteTransition(label = "audio_bars").animateFloat(
                                initialValue = 0.15f,
                                targetValue = 0.95f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(duration, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "wave_bar"
                            )
                            val barHeight = if (playingState) (14 * heightFactor).dp else 2.dp
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(barHeight)
                                    .background(Color(0xFFFF2D55).copy(alpha = 0.85f), RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.playPrev() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.SkipPrevious, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { viewModel.toggleMusicPlay() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (playingState) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = { viewModel.playNext() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
