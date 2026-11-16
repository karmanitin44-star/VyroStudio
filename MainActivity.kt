package com.vyro.studio

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VyroApp() }
    }
}

private val bg = Color(0xFF090B13)
private val panel = Color(0xFF171B2A)
private val violet = Color(0xFF9B7BFF)
private val cyan = Color(0xFF55D6E8)

@Composable
fun VyroApp() {
    var tab by remember { mutableStateOf(0) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPreset by remember { mutableStateOf("VYRO Cinematic 01") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) videoUri = uri
    }
    MaterialTheme(colorScheme = darkColorScheme(
        primary = violet, secondary = cyan, background = bg, surface = panel
    )) {
        Scaffold(containerColor = bg, bottomBar = {
            NavigationBar(containerColor = panel) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 },
                    icon = { Icon(Icons.Default.Movie, null) }, label = { Text("Studio") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 },
                    icon = { Icon(Icons.Default.Palette, null) }, label = { Text("Effects") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 },
                    icon = { Icon(Icons.Default.Tune, null) }, label = { Text("My FX") })
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("VYRO", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                        Text("STUDIO  •  CREATE BEYOND LIMITS", color = cyan, fontSize = 9.sp, letterSpacing = 1.4.sp)
                    }
                    Surface(color = Color(0xFF27213E), shape = RoundedCornerShape(20.dp)) {
                        Text("BETA 0.1", Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = violet, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(20.dp))
                when (tab) {
                    0 -> StudioScreen(videoUri, selectedPreset, onPick = { picker.launch(arrayOf("video/*")) })
                    1 -> EffectsScreen(selectedPreset, onSelect = { selectedPreset = it })
                    else -> MyFxScreen()
                }
            }
        }
    }
}

@Composable
private fun StudioScreen(uri: Uri?, preset: String, onPick: () -> Unit) {
    Text("YOUR CREATIVE WORKSPACE", color = Color.LightGray, fontSize = 11.sp, letterSpacing = 1.sp)
    Spacer(Modifier.height(12.dp))
    Surface(color = panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (uri == null) {
                Box(Modifier.fillMaxWidth().height(205.dp).background(Color(0xFF101422), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Movie, null, tint = violet, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Start with a video", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Import a clip to preview it in VYRO", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            } else {
                VideoPreview(uri, Modifier.fillMaxWidth().height(205.dp))
            }
            Spacer(Modifier.height(14.dp))
            Button(onClick = onPick, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = violet)) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text(if (uri == null) "IMPORT VIDEO" else "CHANGE VIDEO")
            }
        }
    }
    Spacer(Modifier.height(18.dp))
    Text("QUICK TOOLS", color = Color.White, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QuickTile("Trim", "✂", Modifier.weight(1f))
        QuickTile("Filters", "◈", Modifier.weight(1f))
        QuickTile("Text", "T", Modifier.weight(1f))
        QuickTile("Audio", "♫", Modifier.weight(1f))
    }
    Spacer(Modifier.height(18.dp))
    Text("SELECTED LOOK", color = Color.LightGray, fontSize = 11.sp)
    Text(preset, color = cyan, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
    Text("MVP note: video import and playback are enabled. Timeline editing, effect rendering and export are the next engineering milestones.",
        color = Color(0xFFADB4C8), fontSize = 12.sp, lineHeight = 18.sp)
}

@Composable
private fun VideoPreview(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri)); prepare(); playWhenReady = false
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    AndroidView(modifier = modifier, factory = { ctx ->
        PlayerView(ctx).apply { this.player = player; useController = true }
    }, update = { it.player = player })
}

@Composable
private fun QuickTile(title: String, symbol: String, modifier: Modifier = Modifier) {
    Surface(color = panel, shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(Modifier.padding(vertical = 15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, color = violet, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.White, fontSize = 11.sp)
        }
    }
}

private val categories = listOf("Cinematic", "Vintage", "Color", "Glitch", "Light", "Motion", "Dream", "Film", "Neon", "Portrait")
private val presetNames = (1..1000).map { n ->
    val category = categories[(n - 1) % categories.size]
    "$category ${n.toString().padStart(3, '0')}"
}

@Composable
private fun EffectsScreen(selected: String, onSelect: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }
    val shown = remember(query, category) {
        presetNames.filter { (query.isBlank() || it.contains(query, true)) &&
            (category == "All" || it.startsWith(category)) }
    }
    Text("1000+ LOOK PRESETS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Text("A growing preset catalogue for the VYRO effects engine", color = Color.LightGray, fontSize = 12.sp)
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search filters and effects") }, singleLine = true)
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf("All", "Cinematic", "Vintage", "Glitch", "Neon").forEach {
            FilterChip(selected = category == it, onClick = { category = it }, label = { Text(it) })
        }
    }
    Text("${shown.size} presets", color = cyan, fontSize = 12.sp)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        items(shown) { name ->
            Surface(color = if (selected == name) Color(0xFF30264C) else panel,
                shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().clickable { onSelect(name) }) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, null, tint = violet)
                    Spacer(Modifier.width(12.dp))
                    Text(name, color = Color.White, modifier = Modifier.weight(1f))
                    if (selected == name) Text("SELECTED", color = cyan, fontSize = 9.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyFxScreen() {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Color grade") }
    var intensity by remember { mutableFloatStateOf(0.65f) }
    var saved by remember { mutableStateOf(loadRecipes(context)) }
    Column {
        Text("MY EFFECT CREATOR", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Create and save your own effect recipes", color = Color.LightGray, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(),
            label = { Text("Effect name") }, singleLine = true)
        Spacer(Modifier.height(8.dp))
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(value = type, onValueChange = {}, readOnly = true,
                label = { Text("Effect type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth())
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("Color grade", "Glow", "Blur", "Glitch", "Vignette", "Film grain").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { type = it; expanded = false })
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Intensity ${(intensity * 100).toInt()}%", color = cyan)
        Slider(value = intensity, onValueChange = { intensity = it })
        Button(onClick = {
            if (name.isNotBlank()) {
                saved = (saved + "$name • $type • ${(intensity * 100).toInt()}%")
                saveRecipes(context, saved); name = ""
            }
        }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = violet)) {
            Icon(Icons.Default.Add, null); Text(" SAVE MY EFFECT")
        }
        Spacer(Modifier.height(16.dp))
        Text("SAVED EFFECT RECIPES (${saved.size})", color = Color.White, fontWeight = FontWeight.Bold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            items(saved) { recipe ->
                Surface(color = panel, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(recipe, Modifier.padding(13.dp), color = Color.White, fontSize = 13.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("These are saved effect recipes in this MVP. Applying them to rendered video will be added with the GPU effects/export engine.",
            color = Color(0xFFADB4C8), fontSize = 11.sp, lineHeight = 16.sp)
    }
}

private fun loadRecipes(context: android.content.Context): List<String> {
    val raw = context.getSharedPreferences("vyro", 0).getString("recipes", "[]") ?: "[]"
    val arr = JSONArray(raw)
    return (0 until arr.length()).map { arr.getString(it) }
}
private fun saveRecipes(context: android.content.Context, recipes: List<String>) {
    val arr = JSONArray()
    recipes.forEach { arr.put(it) }
    context.getSharedPreferences("vyro", 0).edit().putString("recipes", arr.toString()).apply()
}
