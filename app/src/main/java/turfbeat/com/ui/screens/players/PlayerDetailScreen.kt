package turfbeat.com.ui.screens.players

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    playerId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: PlayerViewModel = koinViewModel()
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(playerId) { viewModel.loadPlayerDetail(playerId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.player?.fullName ?: "Player", fontWeight = FontWeight.Bold, color = Ink) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Pitch)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Failed to load", color = Ink3)
                    TextButton(onClick = { viewModel.loadPlayerDetail(playerId) }) { Text("Retry", color = Pitch2) }
                }
            }
        } else if (state.player != null) {
            val player = state.player!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Bg)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (player.avatarUrl != null) {
                            AsyncImage(
                                model = player.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(96.dp).clip(CircleShape).background(PitchSoft)
                            )
                        } else {
                            Box(
                                modifier = Modifier.size(96.dp).clip(CircleShape).background(PitchSoft),
                                contentAlignment = Alignment.Center
                            ) { Text("⚽", fontSize = 40.sp) }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(player.fullName ?: "Unknown", style = MaterialTheme.typography.headlineMedium, color = Ink, fontWeight = FontWeight.Bold)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            if (player.position != null) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(player.position.replaceFirstChar { it.uppercase() }) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = PitchSoft, labelColor = Forest)
                                )
                            }
                            if (player.skillLevel != null) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(player.skillLevel.replaceFirstChar { it.uppercase() }) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = Panel, labelColor = Ink)
                                )
                            }
                        }

                        if (player.isLookingForMoreClubs) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(shape = RoundedCornerShape(100.dp), color = PitchSoft) {
                                Text(
                                    "Looking for Clubs",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Forest,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Details", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        PlayerDetailRow("Location", listOfNotNull(player.area, player.district, player.division).joinToString(", ").ifEmpty { "Not set" })
                        PlayerDetailRow("Age", player.age?.toString() ?: "Not set")
                        PlayerDetailRow("Email", player.email ?: "Not set")
                        PlayerDetailRow("Phone", player.phone ?: "Not set")
                        PlayerDetailRow("Emergency Contact", player.emergencyContact ?: "Not set")
                        PlayerDetailRow("Injured", if (player.isInjured) "Yes" else "No")
                        PlayerDetailRow("Last Match", player.lastMatchPlayed?.take(10) ?: "N/A")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PlayerDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Ink3)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Medium)
    }
}
