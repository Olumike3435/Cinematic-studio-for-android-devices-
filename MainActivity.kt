package com.cinematicstudio.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val Ink = Color(0xFF090A0D)
private val Panel = Color(0xFF15171D)
private val Panel2 = Color(0xFF20232B)
private val Gold = Color(0xFFE7B85C)
private val Muted = Color(0xFF9A9DA8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CinematicStudioApp() }
    }
}

@Composable
fun CinematicStudioApp() {
    var tab by remember { mutableStateOf("Edit") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var filter by remember { mutableStateOf("Original") }
    var brightness by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var showPremium by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> selectedUri = uri }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Ink, surface = Panel, primary = Gold,
            onPrimary = Ink, onBackground = Color.White, onSurface = Color.White
        )
    ) {
        Scaffold(
            containerColor = Ink,
            bottomBar = {
                NavigationBar(containerColor = Panel, contentColor = Color.White) {
                    listOf(
                        Triple("Home", Icons.Default.Home, "Home"),
                        Triple("Edit", Icons.Default.Tune, "Edit"),
                        Triple("Templates", Icons.Default.AutoAwesome, "Templates"),
                        Triple("Me", Icons.Default.Person, "Me")
                    ).forEach { (label, icon, key) ->
                        NavigationBarItem(
                            selected = tab == key,
                            onClick = { tab = key },
                            icon = { Icon(icon, null) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Gold,
                                selectedTextColor = Gold,
                                indicatorColor = Panel2,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding)
                    .background(Ink)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("CINEMATIC", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text("STUDIO", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF302719),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier.clickable { showPremium = true }
                        ) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Gold, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(5.dp))
                                Text("PRO", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                when (tab) {
                    "Home" -> HomeScreen(
                        onPhoto = { imagePicker.launch("image/*") },
                        onVideo = { tab = "Edit" },
                        onTemplates = { tab = "Templates" }
                    )
                    "Edit" -> EditorScreen(
                        uri = selectedUri,
                        filter = filter,
                        brightness = brightness,
                        saturation = saturation,
                        onPick = { imagePicker.launch("image/*") },
                        onFilter = { filter = it },
                        onBrightness = { brightness = it },
                        onSaturation = { saturation = it }
                    )
                    "Templates" -> TemplatesScreen()
                    else -> ProfileScreen(onPremium = { showPremium = true })
                }
            }
        }

        if (showPremium) {
            AlertDialog(
                onDismissRequest = { showPremium = false },
                containerColor = Panel,
                title = { Text("Cinematic Studio Pro", color = Gold) },
                text = { Text("The app is free to use. Optional Pro tools are planned for advanced AI effects, premium looks and additional export options.", color = Color.White) },
                confirmButton = {
                    TextButton(onClick = { showPremium = false }) { Text("Got it", color = Gold) }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(onPhoto: () -> Unit, onVideo: () -> Unit, onTemplates: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text("Create something cinematic.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Your creative studio, right in your pocket.", color = Muted, modifier = Modifier.padding(top = 5.dp))
        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard("New photo edit", "Import an image", Icons.Default.Image, Modifier.weight(1f), onPhoto)
            ActionCard("New video", "Start a project", Icons.Default.VideoLibrary, Modifier.weight(1f), onVideo)
        }
        Spacer(Modifier.height(18.dp))
        Surface(color = Panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("YOUR CREATIVE TOOLS", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(14.dp))
                ToolRow(Icons.Default.Tune, "Photo adjustments", "Brightness and colour controls")
                ToolRow(Icons.Default.FilterVintage, "Cinematic looks", "Choose a visual mood")
                ToolRow(Icons.Default.Timeline, "Video workspace", "Timeline-style editing preview")
                ToolRow(Icons.Default.AutoAwesome, "AI tools", "Advanced AI features planned")
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(onClick = onTemplates, colors = ButtonDefaults.buttonColors(containerColor = Gold), modifier = Modifier.fillMaxWidth()) {
            Text("Explore templates", color = Ink, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Surface(color = Panel, shape = RoundedCornerShape(20.dp), modifier = modifier.clickable { onClick() }) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Gold, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(18.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
fun EditorScreen(
    uri: Uri?, filter: String, brightness: Float, saturation: Float,
    onPick: () -> Unit, onFilter: (String) -> Unit,
    onBrightness: (Float) -> Unit, onSaturation: (Float) -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PHOTO WORKSPACE", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("DRAFT", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(18.dp))
                .background(Panel2).border(1.dp, Color(0xFF30343E), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                val matrix = ColorMatrix().apply {
                    setToSaturation(saturation)
                    val scale = brightness
                    val values = floatArrayOf(
                        scale,0f,0f,0f,0f, 0f,scale,0f,0f,0f,
                        0f,0f,scale,0f,0f, 0f,0f,0f,1f,0f
                    )
                    set(values)
                }
                AsyncImage(
                    model = uri, contentDescription = "Selected photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.colorMatrix(matrix)
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = Gold, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Your preview appears here", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Choose a photo to start editing", color = Muted, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onPick, colors = ButtonDefaults.buttonColors(containerColor = Gold), modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.FileOpen, null, tint = Ink)
            Spacer(Modifier.width(8.dp))
            Text("Import photo", color = Ink, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Text("CINEMATIC FILTERS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            listOf("Original", "Warm", "Cool", "Noir", "Vintage", "Dream").forEach { name ->
                FilterChip(
                    selected = filter == name, onClick = { onFilter(name) },
                    label = { Text(name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold, selectedLabelColor = Ink,
                        containerColor = Panel, labelColor = Color.White
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("ADJUSTMENTS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
        Text("Brightness", color = Muted, fontSize = 12.sp)
        Slider(value = brightness, onValueChange = onBrightness, valueRange = 0.5f..1.5f, colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = Gold))
        Text("Saturation", color = Muted, fontSize = 12.sp)
        Slider(value = saturation, onValueChange = onSaturation, valueRange = 0f..2f, colors = SliderDefaults.colors(thumbColor = Gold, activeTrackColor = Gold))
        Spacer(Modifier.height(12.dp))
        Surface(color = Panel, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("VIDEO EDITOR", color = Gold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Text("A CapCut-inspired timeline workspace is part of the next build stage. Import, trim, audio mixing and video export need native media-processing implementation.", color = Muted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun TemplatesScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text("Templates & looks", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Starter creative directions for your next edit.", color = Muted, modifier = Modifier.padding(top = 4.dp, bottom = 18.dp))
        listOf(
            "Golden hour" to "Warm highlights • soft contrast",
            "Midnight noir" to "Deep shadows • monochrome mood",
            "Retro film" to "Faded colour • vintage texture",
            "Cool blue" to "Crisp tones • cinematic atmosphere",
            "Portrait glow" to "Soft light • natural skin tones"
        ).forEachIndexed { index, item ->
            Surface(color = Panel, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(listOf(Color(0xFF80552E), Color(0xFF272A35), Color(0xFF77604B), Color(0xFF344C67), Color(0xFF6E5360))[index]))
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.first, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(item.second, color = Muted, fontSize = 11.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Gold)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(onPremium: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("Your studio", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Surface(color = Panel, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("FREE PLAN", color = Gold, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("Create and edit without paying to download the app.", color = Muted, modifier = Modifier.padding(top = 7.dp))
                Spacer(Modifier.height(14.dp))
                OutlinedButton(onClick = onPremium, modifier = Modifier.fillMaxWidth(), border = androidx.compose.foundation.BorderStroke(1.dp, Gold)) {
                    Text("Discover optional Pro features", color = Gold)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Cinematic Studio • Version 1.0 prototype", color = Muted, fontSize = 12.sp)
    }
}

@Composable
fun ToolRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, detail: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Gold, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(detail, color = Muted, fontSize = 11.sp)
        }
    }
}
