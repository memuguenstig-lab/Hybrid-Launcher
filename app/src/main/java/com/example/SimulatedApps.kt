package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// Reusable elegant Glass Card Container
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.25f,
    blurBorder: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF272A35).copy(alpha = alpha),
                        Color(0xFF14151B).copy(alpha = alpha * 1.3f)
                    )
                )
            )
            .drawBehind {
                // Top glossy liquid curve highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = size.width * 0.65f,
                    center = Offset(size.width * 0.2f, -size.height * 0.2f)
                )
            }
            .border(
                1.dp,
                if (blurBorder) Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.32f),
                        Color.White.copy(alpha = 0.04f)
                    )
                ) else SolidColor(Color.White.copy(alpha = 0.12f)),
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        content = content
    )
}

// SIMULATOR MAIN APP SELECTOR
@Composable
fun SimulatedAppContainer(
    app: AppItem,
    viewModel: LauncherViewModel,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0E14)) // Elegant dark solid base
    ) {
        // App Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Simulated Title bar with system indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = app.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Mini indicators
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wifi",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Akku",
                        tint = Color(0xFF34C759),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${viewModel.batteryLevel.collectAsState().value}%",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Divider
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            // Body content routing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (app.iconType) {
                    IconType.PHONE -> PhoneDialerScreen()
                    IconType.WEATHER -> WeatherScreen()
                    IconType.SAFARI -> SafariBrowserScreen()
                    IconType.CAMERA -> CyberCameraScreen()
                    IconType.MUSIC -> FullMusicScreen(viewModel)
                    IconType.SETTINGS -> SettingsScreen(viewModel)
                    IconType.CALCULATOR -> CalculatorScreen()
                    IconType.NOTES -> NotesScreen()
                    IconType.ACTIVITY -> FitnessActivityScreen()
                    else -> GenericAppScreen(app)
                }
            }

            // iOS style navigation gesture bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(5.dp)
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(2.5.dp))
                )
            }
        }
    }
}

// 1. PHONE DIALER SCREEN
@Composable
fun PhoneDialerScreen() {
    var dialNumber by remember { mutableStateOf("") }
    val dialHistory = remember { mutableStateListOf<String>() }
    var inCall by remember { mutableStateOf(false) }
    var callSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(inCall) {
        if (inCall) {
            callSeconds = 0
            while (inCall) {
                delay(1000)
                callSeconds++
            }
        }
    }

    if (inCall) {
        // Active Calling Window
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Contact",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(dialNumber, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Verbunden...", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                Text(
                    String.format("%02d:%02d", callSeconds / 60, callSeconds % 60),
                    color = Color(0xFF34C759),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Calling Controls
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CallOptionButton(icon = Icons.Default.VolumeUp, label = "Lautsprecher")
                    CallOptionButton(icon = Icons.Default.MicOff, label = "Stumm")
                    CallOptionButton(icon = Icons.Default.GridOn, label = "Keypad")
                }
                Spacer(modifier = Modifier.height(30.dp))
                IconButton(
                    onClick = { inCall = false },
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "Auflegen",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    } else {
        // Standard Keypad View
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Dial target Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dialNumber.ifEmpty { "Nummer tippen" },
                        color = if (dialNumber.isEmpty()) Color.White.copy(alpha = 0.4f) else Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (dialNumber.isNotEmpty()) {
                        IconButton(
                            onClick = { dialNumber = dialNumber.dropLast(1) }
                        ) {
                            Icon(Icons.Default.Backspace, "Löschen", tint = Color.LightGray)
                        }
                    }
                }
            }

            // Keypad Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val keys = listOf(
                    listOf("1", ""), listOf("2", "A B C"), listOf("3", "D E F"),
                    listOf("4", "G H I"), listOf("5", "J K L"), listOf("6", "M N O"),
                    listOf("7", "P Q R S"), listOf("8", "T U V"), listOf("9", "W X Y Z"),
                    listOf("*", ""), listOf("0", "+"), listOf("#", "")
                )

                for (row in 0..3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..2) {
                            val data = keys[row * 3 + col]
                            DialKeyboardButton(
                                number = data[0],
                                alphabet = data[1],
                                onClick = { dialNumber += data[0] }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        enabled = dialNumber.isNotEmpty(),
                        onClick = {
                            if (dialNumber.isNotEmpty()) {
                                dialHistory.add(0, dialNumber)
                                inCall = true
                            }
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                if (dialNumber.isEmpty()) Color.Gray.copy(alpha = 0.4f) else Color(0xFF34C759),
                                CircleShape
                            )
                    ) {
                        Icon(Icons.Default.Call, "Anrufen", tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
            }

            // History Log Pill
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Verlauf:",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                if (dialHistory.isEmpty()) {
                    Text("Keine Anrufe getätigt", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(dialHistory.take(5)) { num ->
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CallMade, null, tint = Color(0xFF34C759), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(num, color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialKeyboardButton(number: String, alphabet: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(number, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
            if (alphabet.isNotEmpty()) {
                Text(alphabet, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CallOptionButton(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.LightGray, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

// 2. WEATHER SCREEN
@Composable
fun WeatherScreen() {
    var isCelsius by remember { mutableStateOf(true) }
    var selectedCityIdx by remember { mutableStateOf(0) }

    data class WeatherCity(
        val name: String,
        val temp: Int,
        val status: String,
        val icon: ImageVector,
        val wind: Int,
        val hum: Int,
        val color: Brush
    )

    val cities = listOf(
        WeatherCity(
            "Berlin", 18, "Wolkig", Icons.Default.Cloud, 12, 65,
            Brush.verticalGradient(listOf(Color(0xFF5AC8FA), Color(0xFF007AFF)))
        ),
        WeatherCity(
            "München", 22, "Sonnig", Icons.Default.WbSunny, 8, 45,
            Brush.verticalGradient(listOf(Color(0xFFFF9500), Color(0xFFFFCC00)))
        ),
        WeatherCity(
            "Hamburg", 14, "Regnerisch", Icons.Default.BeachAccess, 24, 82,
            Brush.verticalGradient(listOf(Color(0xFF4A4B50), Color(0xFF2E3033)))
        )
    )

    val currentCity = cities[selectedCityIdx]
    val displayedTemp = if (isCelsius) currentCity.temp else (currentCity.temp * 9 / 5) + 32
    val tempUnit = if (isCelsius) "°C" else "°F"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentCity.color)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // City buttons selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                cities.forEachIndexed { idx, city ->
                    Button(
                        onClick = { selectedCityIdx = idx },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCityIdx == idx) Color.White else Color.White.copy(alpha = 0.2f),
                            contentColor = if (selectedCityIdx == idx) Color.DarkGray else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(city.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Metric switch
            TextButton(onClick = { isCelsius = !isCelsius }) {
                Text(if (isCelsius) "Zu °F wechseln" else "Zu °C wechseln", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Mid Display
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = currentCity.icon,
                contentDescription = currentCity.status,
                tint = Color.White,
                modifier = Modifier
                    .size(90.dp)
                    .drawBehind {
                        drawCircle(Color.White.copy(alpha = 0.15f), radius = size.minDimension * 0.7f)
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(currentCity.name, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.Top) {
                Text("$displayedTemp", color = Color.White, fontSize = 80.sp, fontWeight = FontWeight.Black)
                Text(tempUnit, color = Color.White.copy(alpha = 0.8f), fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            }
            Text(currentCity.status, color = Color.White.copy(alpha = 0.9f), fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }

        // Sub stats cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Air, "Wind", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Wind", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        Text("${currentCity.wind} km/h", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, "Hum", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Humidity", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        Text("${currentCity.hum}%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Hourly forecast
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "STÜNDLICHE VORHERSAGE",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val hourly = listOf(
                    Pair("09:00", 1), Pair("11:00", 0), Pair("13:00", 1),
                    Pair("15:00", 2), Pair("17:00", 1), Pair("19:00", 0)
                )

                items(hourly) { (time, offsetTemp) ->
                    val forecastTemp = currentCity.temp + offsetTemp
                    val displayedForecastTemp = if (isCelsius) forecastTemp else (forecastTemp * 9 / 5) + 32
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(time, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(currentCity.icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$displayedForecastTemp°", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 3. SAFARI BROWSER SIMULATOR
@Composable
fun SafariBrowserScreen() {
    var urlText by remember { mutableStateOf("https://www.google.de") }
    var loadedUrl by remember { mutableStateOf("https://www.google.de") }
    val history = remember { mutableStateListOf<String>("https://www.google.de") }
    var historyIndex by remember { mutableStateOf(0) }

    val loadUrl = {
        val verifiedUrl = if (urlText.startsWith("http")) urlText else "https://$urlText"
        if (verifiedUrl != loadedUrl) {
            loadedUrl = verifiedUrl
            history.add(verifiedUrl)
            historyIndex = history.size - 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE)) // Safari Slate light theme container
    ) {
        // Control Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF6F6F6))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = {
                        if (historyIndex > 0) {
                            historyIndex--
                            loadedUrl = history[historyIndex]
                            urlText = loadedUrl
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = historyIndex > 0
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Zurück", tint = if (historyIndex > 0) Color.DarkGray else Color.LightGray)
                }

                IconButton(
                    onClick = {
                        if (historyIndex < history.size - 1) {
                            historyIndex++
                            loadedUrl = history[historyIndex]
                            urlText = loadedUrl
                        }
                    },
                    modifier = Modifier.size(36.dp),
                    enabled = historyIndex < history.size - 1
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Vorwärts", tint = if (historyIndex < history.size - 1) Color.DarkGray else Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // URL input box
            TextField(
                value = urlText,
                onValueChange = { urlText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 13.sp, color = Color.DarkGray),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFE5E5E7),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Security, "Secure", tint = Color(0xFF34C759), modifier = Modifier.size(14.dp)) },
                trailingIcon = {
                    if (urlText.isNotEmpty()) {
                        IconButton(onClick = { loadUrl() }) {
                            Icon(Icons.Default.ArrowForward, "Suchen", tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true
            )
        }

        // Display Mock Page Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .padding(16.dp)
        ) {
            when {
                loadedUrl.contains("google") -> SafariGoogleScreen(onSearch = { query ->
                    urlText = "google.de/search?q=$query"
                    loadedUrl = "google.de/search?q=$query"
                })
                loadedUrl.contains("search") -> SafariSearchResultsScreen(
                    query = loadedUrl.substringAfter("q=").replace("+", " ").replace("%20", " "),
                    onResultClick = { title, link ->
                        urlText = link
                        loadedUrl = link
                    }
                )
                loadedUrl.contains("apple.com") -> SafariAppleScreen()
                loadedUrl.contains("android.com") -> SafariAndroidScreen()
                else -> SafariGenericWebScreen(loadedUrl)
            }
        }
    }
}

@Composable
fun SafariGoogleScreen(onSearch: (String) -> Unit) {
    var q by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Google",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            style = TextStyle(
                shadow = Shadow(color = Color.LightGray, offset = Offset(2f, 2f), blurRadius = 2f)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = q,
            onValueChange = { q = it },
            placeholder = { Text("Im Web suchen...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp),
            trailingIcon = {
                IconButton(onClick = { if (q.isNotEmpty()) onSearch(q) }) {
                    Icon(Icons.Default.Search, "Suchen", tint = Color.LightGray)
                }
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { if (q.isNotEmpty()) onSearch(q) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5), contentColor = Color.DarkGray)
            ) {
                Text("Google Suche", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SafariSearchResultsScreen(query: String, onResultClick: (String, String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Suchergebnisse für \"$query\":", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        val itemsList = listOf(
            Pair("Apple - Das neue iOS 18 features", "https://apple.com"),
            Pair("Android - Offizielle Neuerungen & Upgrades", "https://android.com"),
            Pair("Warum modern hybrid Launcher am besten sind", "https://launcher-blog.de"),
            Pair("Jetpack Compose im Android-Launcher nutzen", "https://compose-wiki.org")
        )

        items(itemsList) { (title, link) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onResultClick(title, link) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF9F6))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(title, color = Color(0xFF1A0DAB), fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                    Text(link, color = Color(0xFF006621), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Entdecken Sie moderne responsive Designs mit hochauflösendem UI-Framework & Glassmorphism.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SafariAppleScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Smartphone, "Apple Product", tint = Color.White, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("iPhone 16 Pro", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("Entwickelt für Apple Intelligence.", color = Color.Gray, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("MacBook Pro M4", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Unfassbare Power. Perfekte Effizienz.", color = Color.DarkGray, fontSize = 13.sp)
    }
}

@Composable
fun SafariAndroidScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3DDC84))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Android, "Android", tint = Color.White, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Android 15", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text("Dein Betriebssystem. Ganz individuell.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Jetpack Compose 1.7", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Modernes, reaktives UI-Layout.", color = Color.DarkGray, fontSize = 13.sp)
    }
}

@Composable
fun SafariGenericWebScreen(url: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Public, "Public Web", tint = Color.Gray, modifier = Modifier.size(60.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Internet-Verbindung simuliert!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Geladene URL: $url",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// 4. CYBER CAMERA SCREEN
@Composable
fun CyberCameraScreen() {
    var filterIdx by remember { mutableStateOf(0) }
    var scaleStep by remember { mutableStateOf(1.0f) }
    var shutterAnimationDone by remember { mutableStateOf(true) }
    val filters = listOf(
        Pair("Standard", Color.White),
        Pair("Noir", Color.Gray),
        Pair("Vaporwave", Color(0xFFFF7DF3)),
        Pair("Sepia", Color(0xFFD2B48C)),
        Pair("Cyberpunk", Color(0xFF00FFCC))
    )

    LaunchedEffect(shutterAnimationDone) {
        if (!shutterAnimationDone) {
            delay(200)
            shutterAnimationDone = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper utilities
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Simulated toggle */ }) {
                Icon(Icons.Default.FlashOn, "Flash", tint = Color.White)
            }
            Text("RE-FLEX LENS 66mm", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { scaleStep = if (scaleStep == 1.0f) 2.0f else 1.0f }) {
                Text("${scaleStep}x", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).padding(6.dp))
            }
        }

        // Camera Preview Canvas Simulation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF222530))
        ) {
            // Simulated tracker target rendering
            var pulseValue by remember { mutableStateOf(0f) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(300)
                    pulseValue = if (pulseValue == 0f) 1f else 0f
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val circleColor = filters[filterIdx].second.copy(alpha = 0.4f)
                // Simulated focus box in middle
                val sizeVal = 80.dp.toPx()
                val focusOffsetValue = pulseValue * 15f
                val strokeW = 2.dp.toPx()

                // Corner bracket simulation
                // Top-Left
                drawRect(color = circleColor, topLeft = Offset((size.width - sizeVal)/2f - focusOffsetValue, (size.height - sizeVal)/2f - focusOffsetValue), size = Size(10.dp.toPx(), strokeW))
                drawRect(color = circleColor, topLeft = Offset((size.width - sizeVal)/2f - focusOffsetValue, (size.height - sizeVal)/2f - focusOffsetValue), size = Size(strokeW, 10.dp.toPx()))

                // Top-Right
                drawRect(color = circleColor, topLeft = Offset((size.width + sizeVal)/2f - 10.dp.toPx() + focusOffsetValue, (size.height - sizeVal)/2f - focusOffsetValue), size = Size(10.dp.toPx(), strokeW))
                drawRect(color = circleColor, topLeft = Offset((size.width + sizeVal)/2f + focusOffsetValue, (size.height - sizeVal)/2f - focusOffsetValue), size = Size(strokeW, 10.dp.toPx()))

                // Bottom-Left
                drawRect(color = circleColor, topLeft = Offset((size.width - sizeVal)/2f - focusOffsetValue, (size.height + sizeVal)/2f + focusOffsetValue), size = Size(10.dp.toPx(), strokeW))
                drawRect(color = circleColor, topLeft = Offset((size.width - sizeVal)/2f - focusOffsetValue, (size.height + sizeVal)/2f - 10.dp.toPx() + focusOffsetValue), size = Size(strokeW, 10.dp.toPx()))

                // Bottom-Right
                drawRect(color = circleColor, topLeft = Offset((size.width + sizeVal)/2f - 10.dp.toPx() + focusOffsetValue, (size.height + sizeVal)/2f + focusOffsetValue), size = Size(10.dp.toPx(), strokeW))
                drawRect(color = circleColor, topLeft = Offset((size.width + sizeVal)/2f + focusOffsetValue, (size.height + sizeVal)/2f - 10.dp.toPx() + focusOffsetValue), size = Size(strokeW, 10.dp.toPx()))
            }

            // Stylized cyber landscape overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Hue overlay according to selection
                        drawRect(
                            color = filters[filterIdx].second.copy(alpha = 0.15f)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    )
                    Text("KAMERA PREVIEW ACTIVATED", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Shutter capture flash animation overlay
            if (!shutterAnimationDone) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }
        }

        // Camera Controls and Filters Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            // Filters Horizontal List
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                itemsIndexed(filters) { idx, filter ->
                    val isSelected = filterIdx == idx
                    Button(
                        onClick = { filterIdx = idx },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                            contentColor = if (isSelected) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(filter.first, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Capture Shutter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhotoLibrary, "Gallery", tint = Color.White)
                }

                // Shutter trigger circular button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .border(3.dp, Color.White, CircleShape)
                        .padding(5.dp)
                        .background(Color.Transparent, CircleShape)
                        .clip(CircleShape)
                        .clickable { shutterAnimationDone = false },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White, CircleShape)
                    )
                }

                IconButton(
                    onClick = { filterIdx = (filterIdx + 1) % filters.size },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.Default.Cached, "Flip", tint = Color.White)
                }
            }
        }
    }
}

// 5. FULL MUSIC SCREEN
@Composable
fun FullMusicScreen(viewModel: LauncherViewModel) {
    val currentTrackIdx = viewModel.musicTrackIndex.collectAsState().value
    val isPlaying = viewModel.musicPlaying.collectAsState().value
    val textState = viewModel.soundtracks[currentTrackIdx]

    // Disk Rotation state animation
    val rotationVal by animateFloatAsState(
        targetValue = if (isPlaying) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiskRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFF2D55).copy(alpha = 0.4f),
                        Color(0xFF0C0E14)
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Spinning holographic record disk
        Box(
            modifier = Modifier
                .size(240.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF2D55),
                                Color(0xFF6A11CB),
                                Color(0xFF2575FC),
                                Color.Black
                            )
                        ),
                        radius = size.width / 2f
                    )
                }
                .rotate(if (isPlaying) rotationVal else 0f),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl grooves
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                drawCircle(color = Color.White.copy(alpha = 0.08f), radius = size.width / 2.3f, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                drawCircle(color = Color.White.copy(alpha = 0.08f), radius = size.width / 2.8f, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                drawCircle(color = Color.White.copy(alpha = 0.1f), radius = size.width / 3.4f, style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
            }
            // Record circle label
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF0C0E14), CircleShape)
                )
            }
        }

        // Title Artists Information
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                textState.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Text(
                textState.artist,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        // Interactive Dancing Audio Visualizer Bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val totalBars = 16
            for (i in 0 until totalBars) {
                var randomHeightMultiplier by remember { mutableStateOf(1f) }
                LaunchedEffect(isPlaying) {
                    if (isPlaying) {
                        while (true) {
                            delay(120)
                            randomHeightMultiplier = Random.nextFloat()
                        }
                    } else {
                        randomHeightMultiplier = 0.1f
                    }
                }
                val barHeight by animateFloatAsState(
                    targetValue = 8f + (32f * randomHeightMultiplier),
                    animationSpec = tween(120, easing = LinearOutSlowInEasing),
                    label = "BarHeight"
                )
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(barHeight.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFF2D55), Color(0xFFFF5252))
                            ),
                            RoundedCornerShape(3.dp)
                        )
                )
            }
        }

        // Progress Controls Sliders
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = if (isPlaying) 0.45f else 0.12f,
                onValueChange = {},
                colors = SliderDefaults.colors(
                    activeTrackColor = Color(0xFFFF2D55),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                    thumbColor = Color.White
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (isPlaying) "01:26" else "00:22", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                Text(textState.duration, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }

        // Action Keys Play, Pause, Back, Prev
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.playPrev() },
                modifier = Modifier.size(54.dp)
            ) {
                Icon(Icons.Default.SkipPrevious, "Prev", tint = Color.White, modifier = Modifier.size(36.dp))
            }

            IconButton(
                onClick = { viewModel.toggleMusicPlay() },
                modifier = Modifier
                    .size(76.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "PlayPause",
                    tint = Color(0xFFFF2D55),
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(
                onClick = { viewModel.playNext() },
                modifier = Modifier.size(54.dp)
            ) {
                Icon(Icons.Default.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

// 6. LAUNCHER PREFERENCES AND BUILDERS
@Composable
fun SettingsScreen(viewModel: LauncherViewModel) {
    val activeCols by viewModel.gridColumns.collectAsState()
    val activeRows by viewModel.gridRows.collectAsState()
    val iconSize by viewModel.iconSize.collectAsState()
    val activeWidgets by viewModel.activeWidgets.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val accentColorHex by viewModel.accentColorHex.collectAsState()
    val currentIconPack by viewModel.currentIconPack.collectAsState()
    val downloadedPacks by viewModel.downloadedIconPacks.collectAsState()
    val volume by viewModel.volumeLevel.collectAsState()
    val brightness by viewModel.brightnessLevel.collectAsState()

    var simulatedDownloadPack by remember { mutableStateOf<String?>(null) }
    var downloadProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(simulatedDownloadPack) {
        if (simulatedDownloadPack != null) {
            downloadProgress = 0f
            while (downloadProgress < 1.0f) {
                delay(100)
                downloadProgress += 0.2f
            }
            viewModel.addDownloadedPack(simulatedDownloadPack!!)
            viewModel.selectIconPack(simulatedDownloadPack!!)
            simulatedDownloadPack = null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Preference Header Block
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(android.graphics.Color.parseColor(accentColorHex)),
                                        Color(0xFF007AFF)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Launcher Studio", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Liquid Glass Edition v2.5", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }

        // 1. GRID & ACCENT CONFIGURATION
        item {
            Text("GRID & GRÖSSE ANPASSEN", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Columns Configuration
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Spalten-Anzahl (Columns)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("$activeCols Columns", color = Color(0xFF007AFF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (cols in 3..6) {
                                val active = cols == activeCols
                                Button(
                                    onClick = { viewModel.setGridColumns(cols) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (active) Color(android.graphics.Color.parseColor(accentColorHex)) else Color.White.copy(alpha = 0.08f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("$cols", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                    // Rows Configuration
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Zeilen-Anzahl (Rows)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("$activeRows Rows", color = Color(0xFF34C759), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (rows in 3..6) {
                                val active = rows == activeRows
                                Button(
                                    onClick = { viewModel.setGridRows(rows) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (active) Color(android.graphics.Color.parseColor(accentColorHex)) else Color.White.copy(alpha = 0.08f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("$rows", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                    // App Icon size customization slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Symbol-Größe (Icon Size)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${iconSize.toInt()} dp", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Slider(
                            value = iconSize,
                            onValueChange = { viewModel.setIconSize(it) },
                            valueRange = 40f..75f,
                            colors = SliderDefaults.colors(
                                activeTrackColor = Color(android.graphics.Color.parseColor(accentColorHex)),
                                inactiveTrackColor = Color.White.copy(alpha = 0.15f),
                                thumbColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 2. THEME ENGINE & ACCENT COLOR
        item {
            Text("FARBE & ELEMENT DESIGN", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // UI Theme Selection
                    Column {
                        Text("System-Thema", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        val themes = listOf("Liquid Glass Glow", "Cyberpunk Matte", "Elegant Dark")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            themes.forEach { theme ->
                                val active = theme == currentTheme
                                Button(
                                    onClick = { viewModel.selectTheme(theme) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (active) Color(android.graphics.Color.parseColor(accentColorHex)) else Color.White.copy(alpha = 0.08f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(theme.substringBefore(" "), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                    // Accent Circle Swatches
                    Column {
                        Text("Akzent-Farbe", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        val colors = listOf(
                            "#007AFF" to "Classic",
                            "#34C759" to "Green",
                            "#FF2D55" to "Pink",
                            "#00FFCC" to "Neon",
                            "#FF9500" to "Orange",
                            "#AF52DE" to "Vapor"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            colors.forEach { (hex, label) ->
                                val active = hex.equals(accentColorHex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(android.graphics.Color.parseColor(hex)), CircleShape)
                                        .border(
                                            width = 3.dp,
                                            color = if (active) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.selectAccentColor(hex) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (active) {
                                        Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. ICON PACK DOWNLOAD MANAGER
        item {
            Text("ICON PACKS & HUB", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Installierte Icon-Pakete", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    
                    // List available packs
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        downloadedPacks.forEach { pack ->
                            val isSelected = pack == currentIconPack
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color.White.copy(alpha = 0.08f) else Color.Transparent)
                                    .clickable { viewModel.selectIconPack(pack) }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(android.graphics.Color.parseColor(accentColorHex)) else Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = pack,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(android.graphics.Color.parseColor(accentColorHex)).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Aktiviert", color = Color(android.graphics.Color.parseColor(accentColorHex)), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                    // Store (Simulated downloading)
                    Column {
                        Text("Online Icon Pack Store (Simuliert)", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        val availableToDownload = listOf("Unicons Line Pack")
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            availableToDownload.forEach { pack ->
                                val isDownloaded = downloadedPacks.contains(pack)
                                val isCurrentlyDownloading = simulatedDownloadPack == pack

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(pack, color = Color.White, fontSize = 13.sp)
                                    
                                    if (isDownloaded) {
                                        Text("Bereits geladen", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    } else if (isCurrentlyDownloading) {
                                        // Circular or Linear loader representation
                                        Box(modifier = Modifier.width(100.dp)) {
                                            LinearProgressIndicator(
                                                progress = { downloadProgress },
                                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                color = Color(android.graphics.Color.parseColor(accentColorHex)),
                                                trackColor = Color.White.copy(alpha = 0.15f)
                                            )
                                        }
                                    } else {
                                        Button(
                                            onClick = { simulatedDownloadPack = pack },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Icon(Icons.Default.Download, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Laden", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. DYNAMIC WIDGET SYSTEM MANAGER
        item {
            Text("DYNAMISCHE WIDGETS MANAGER", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Widgets hinzufügen, sortieren oder löschen", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    
                    val allWidgetsList = listOf(
                        WidgetType.AESTHETIC_CLOCK to "Uhr & Kalender (System Standard)",
                        WidgetType.SYSTEM_BATTERY to "Ringe: Prozessor, Akku & Speicher",
                        WidgetType.QUICK_SETTINGS to "Schieberegler & Schnelleinstellungen",
                        WidgetType.WEATHER_CARD to "Wettervorhersage München Pill",
                        WidgetType.MUSIC_PLAYER to "Musik-Player (Anzeige & Wellen-Animation)"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allWidgetsList.forEach { (type, description) ->
                            val isActive = activeWidgets.contains(type)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(
                                        text = description.substringBefore(" ("),
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (description.contains("(")) "(" + description.substringAfter("(") else "",
                                        color = Color.White.copy(alpha = 0.45f),
                                        fontSize = 10.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isActive) {
                                        // Reposition Up Button
                                        IconButton(
                                            onClick = { viewModel.repositionWidgetUp(type) },
                                            modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.07f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.KeyboardArrowUp, "Hoch", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                        
                                        // Reposition Down Button
                                        IconButton(
                                            onClick = { viewModel.repositionWidgetDown(type) },
                                            modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.07f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.KeyboardArrowDown, "Runter", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }

                                        // Remove Button
                                        IconButton(
                                            onClick = { viewModel.removeWidget(type) },
                                            modifier = Modifier.size(28.dp).background(Color(0xFFFF2D55).copy(alpha = 0.2f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Delete, "Löschen", tint = Color(0xFFFF2D55), modifier = Modifier.size(16.dp))
                                        }
                                    } else {
                                        // Add Button
                                        Button(
                                            onClick = { viewModel.addWidget(type) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34C759)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Aktivieren", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. LIVE STATS CARD (Simulated telemetry stats)
        item {
            Text("TELEMETRIE STATUS", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Geräte-Akku:", color = Color.White, fontSize = 13.sp)
                        Text("${viewModel.batteryLevel.collectAsState().value}%", color = Color(0xFF34C759), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Prozessor-Auslastung (CPU):", color = Color.White, fontSize = 13.sp)
                        Text("${viewModel.cpuUsage.collectAsState().value}%", color = Color(0xFFFF9500), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Arbeitsspeicher (RAM):", color = Color.White, fontSize = 13.sp)
                        Text("${viewModel.ramUsage.collectAsState().value} GB / 8 GB", color = Color(0xFF5AC8FA), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 6. WALLPAPERS PICKER directly inside Launcher Preferences!
        item {
            val list = WallpaperEngine.wallpapers
            val activeIdx = viewModel.currentWallpaperIndex.collectAsState().value
            Text("WALLPAPER HINTERGRÜNDE", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(list) { idx, wall ->
                    val active = idx == activeIdx
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.selectWallpaper(idx) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp, 120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(wall.brush)
                                .border(
                                    3.dp,
                                    if (active) Color(android.graphics.Color.parseColor(accentColorHex)) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(wall.name, color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// 7. INTERACTIVE APP WORKSPACE COMPILATION FALLBACKS
@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var operatorState by remember { mutableStateOf<String?>(null) }
    var isEnteringNextNumber by remember { mutableStateOf(false) }

    val handleNum = { num: String ->
        if (display == "0" || isEnteringNextNumber) {
            display = num
            isEnteringNextNumber = false
        } else {
            display += num
        }
    }

    val handleOp = { op: String ->
        operand1 = display.toDoubleOrNull()
        operatorState = op
        isEnteringNextNumber = true
    }

    val calculate = {
        val op = operand1
        val opr = operatorState
        val op2 = display.toDoubleOrNull()
        if (op != null && opr != null && op2 != null) {
            val res = when (opr) {
                "+" -> op + op2
                "-" -> op - op2
                "*" -> op * op2
                "/" -> if (op2 != 0.0) op / op2 else Double.NaN
                else -> 0.0
            }
            display = if (res % 1 == 0.0) res.toInt().toString() else res.toString()
            operatorState = null
            operand1 = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Output Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(display, color = Color.White, fontSize = 66.sp, fontWeight = FontWeight.Light, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        // Functional matrix buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val buttons = listOf(
                listOf("C", "±", "%", "/"),
                listOf("7", "8", "9", "*"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", "", ",", "=")
            )

            for (row in buttons) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (item in row) {
                        if (item.isEmpty()) continue
                        val isOperator = item in listOf("/", "*", "-", "+", "=")
                        val isAction = item in listOf("C", "±", "%")
                        val weight = if (item == "0") 2f else 1f

                        Box(
                            modifier = Modifier
                                .weight(weight)
                                .height(68.dp)
                                .clip(RoundedCornerShape(34.dp))
                                .background(
                                    when {
                                        isOperator -> Color(0xFFFF9500)
                                        isAction -> Color(0xFFA5A5A5)
                                        else -> Color(0xFF333333)
                                    }
                                )
                                .clickable {
                                    when {
                                        item == "C" -> {
                                            display = "0"
                                            operand1 = null
                                            operatorState = null
                                        }
                                        item == "=" -> calculate()
                                        item in listOf("+", "-", "*", "/") -> handleOp(item)
                                        item in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9") -> handleNum(item)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                item,
                                color = if (isAction) Color.Black else Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotesScreen() {
    var activeNoteText by remember { mutableStateOf("") }
    val notesList = remember { mutableStateListOf<String>("Einkaufsliste: Milch, Kaffee", "Launcher Projekt abschließen") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6)) // Notes texture background
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("MEINE NOTIZEN", color = Color(0xFF8E8456), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = activeNoteText,
                onValueChange = { activeNoteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Schnelle Notiz tippen...", color = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9500),
                    unfocusedBorderColor = Color.LightGray,
                    focusedTextColor = Color.DarkGray
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (activeNoteText.isNotEmpty()) {
                                notesList.add(0, activeNoteText)
                                activeNoteText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, "Hinzufügen", tint = Color(0xFFFF9500))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(notesList) { note ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(note, color = Color.DarkGray, fontSize = 14.sp)
                            IconButton(onClick = { notesList.remove(note) }) {
                                Icon(Icons.Default.DeleteSweep, "Delete", tint = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FitnessActivityScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("FITNESS RINGE", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(24.dp))

            // Awesome Canvas drawn tri-ring activity loops
            Canvas(modifier = Modifier.size(200.dp)) {
                val cen = Offset(size.width / 2f, size.height / 2f)

                // Red Row Base
                drawArc(Color(0xFFFF2D55).copy(alpha = 0.15f), 0f, 360f, false, topLeft = Offset(10.dp.toPx(), 10.dp.toPx()), size = Size(size.width - 20.dp.toPx(), size.height - 20.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(14.dp.toPx()))
                drawArc(Color(0xFFFF2D55), -90f, 290f, false, topLeft = Offset(10.dp.toPx(), 10.dp.toPx()), size = Size(size.width - 20.dp.toPx(), size.height - 20.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(14.dp.toPx()))

                // Green Row Base
                drawArc(Color(0xFF34C759).copy(alpha = 0.15f), 0f, 360f, false, topLeft = Offset(30.dp.toPx(), 30.dp.toPx()), size = Size(size.width - 60.dp.toPx(), size.height - 60.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(14.dp.toPx()))
                drawArc(Color(0xFF34C759), -90f, 180f, false, topLeft = Offset(30.dp.toPx(), 30.dp.toPx()), size = Size(size.width - 60.dp.toPx(), size.height - 60.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(14.dp.toPx()))

                // Blue Row Base
                drawArc(Color(0xFF007AFF).copy(alpha = 0.15f), 0f, 360f, false, topLeft = Offset(50.dp.toPx(), 50.dp.toPx()), size = Size(size.width - 100.dp.toPx(), size.height - 100.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(14.dp.toPx()))
                drawArc(Color(0xFF007AFF), -90f, 240f, false, topLeft = Offset(50.dp.toPx(), 50.dp.toPx()), size = Size(size.width - 100.dp.toPx(), size.height - 100.dp.toPx()), style = androidx.compose.ui.graphics.drawscope.Stroke(14.dp.toPx()))
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActivityStatsLine(color = Color(0xFFFF2D55), title = "Bewegen (Aktivität)", value = "380 / 500 kcal")
            ActivityStatsLine(color = Color(0xFF34C759), title = "Trainieren", value = "18 / 30 min")
            ActivityStatsLine(color = Color(0xFF007AFF), title = "Stehen", value = "8 / 12 std")
        }
    }
}

@Composable
fun ActivityStatsLine(color: Color, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, color = Color.White, fontSize = 13.sp)
        }
        Text(value, color = Color.LightGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GenericAppScreen(app: AppItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Apps, "App icon", tint = Color.LightGray, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Virtuelle Sub-App", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("App Name: ${app.name}", color = Color.LightGray, fontSize = 14.sp)
        if (app.packageName != null) {
            Text("Paket: ${app.packageName}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
