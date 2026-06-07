package turfbeat.com.ui.screens.players

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.data.model.UserDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.PlayerViewModel
import turfbeat.com.util.Locations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDirectoryScreen(
    onNavigateBack: () -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    val viewModel: PlayerViewModel = koinViewModel()
    val state by viewModel.listState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadPlayers() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Players", fontWeight = FontWeight.Bold, color = Ink) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showFilters = true }) {
                        Text("Filter", color = if (state.selectedPosition != null) Pitch2 else Ink3)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Bg).padding(padding)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it; viewModel.searchPlayers(it) },
                placeholder = { Text("Search players...", color = Ink3) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Pitch, cursorColor = Pitch,
                    focusedContainerColor = Surface, unfocusedContainerColor = Surface
                ),
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        TextButton(onClick = { searchText = ""; viewModel.searchPlayers("") }) {
                            Text("Clear", color = Ink3)
                        }
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.selectedPosition != null) {
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.filterByPosition(null) },
                        label = { Text(state.selectedPosition!!) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PitchSoft, selectedLabelColor = Forest)
                    )
                }
                if (state.selectedSkill != null) {
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.filterBySkill(null) },
                        label = { Text(state.selectedSkill!!) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PitchSoft, selectedLabelColor = Forest)
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Pitch)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Something went wrong", color = Ink3)
                        TextButton(onClick = { viewModel.loadPlayers() }) { Text("Retry", color = Pitch2) }
                    }
                }
            } else if (state.players.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👤", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No players found", style = MaterialTheme.typography.titleMedium, color = Ink3)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.players, key = { it.id }) { player ->
                        PlayerCard(player = player, onClick = { onPlayerClick(player.id) })
                    }
                }
            }
        }
    }

    if (showFilters) {
        AlertDialog(
            onDismissRequest = { showFilters = false },
            title = { Text("Filter Players") },
            text = {
                Column {
                    Text("Position", style = MaterialTheme.typography.labelLarge, color = Forest, fontWeight = FontWeight.Bold)
                    Locations.positions.forEach { pos ->
                        TextButton(onClick = {
                            viewModel.filterByPosition(pos.lowercase())
                            showFilters = false
                        }) { Text(pos, color = Ink) }
                    }
                    TextButton(onClick = {
                        viewModel.filterByPosition(null)
                        showFilters = false
                    }) { Text("Any Position", color = Ink3) }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Skill Level", style = MaterialTheme.typography.labelLarge, color = Forest, fontWeight = FontWeight.Bold)
                    Locations.skillLevels.forEach { skill ->
                        TextButton(onClick = {
                            viewModel.filterBySkill(skill.lowercase())
                            showFilters = false
                        }) { Text(skill, color = Ink) }
                    }
                    TextButton(onClick = {
                        viewModel.filterBySkill(null)
                        showFilters = false
                    }) { Text("Any Level", color = Ink3) }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun PlayerCard(player: UserDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            if (player.avatarUrl != null) {
                AsyncImage(
                    model = player.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(PitchSoft)
                )
            } else {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(PitchSoft),
                    contentAlignment = Alignment.Center
                ) { Text("⚽", fontSize = 22.sp) }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(player.fullName ?: "Unknown", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                Row {
                    if (player.position != null) {
                        Text(player.position.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = Ink3)
                    }
                    if (player.skillLevel != null) {
                        Text(" · ${player.skillLevel.replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.bodySmall, color = Ink3)
                    }
                }
                Text(player.area ?: player.district ?: "", style = MaterialTheme.typography.bodySmall, color = Ink3)
            }

            if (player.isLookingForMoreClubs) {
                Surface(shape = RoundedCornerShape(100.dp), color = PitchSoft) {
                    Text("Open", style = MaterialTheme.typography.labelSmall, color = Forest, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
        }
    }
}
