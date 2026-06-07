package turfbeat.com.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.MatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: MatchViewModel = koinViewModel()
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(matchId) { viewModel.loadMatchDetail(matchId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match", fontWeight = FontWeight.Bold, color = Ink) },
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
                    TextButton(onClick = { viewModel.loadMatchDetail(matchId) }) { Text("Retry", color = Pitch2) }
                }
            }
        } else if (state.match != null) {
            val match = state.match!!
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
                        MatchStatusBadge(status = match.status)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    match.homeClub?.name ?: "Home Club",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Ink,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Text("VS", style = MaterialTheme.typography.headlineLarge, color = Pitch2, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    match.awayClub?.name ?: "TBD",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = if (match.awayClub != null) Ink else Pitch2,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        if (match.homeGoals != null && match.awayGoals != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(shape = RoundedCornerShape(100.dp), color = Forest) {
                                Text(
                                    "${match.homeGoals} - ${match.awayGoals}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Surface,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
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

                        MatchDetailRow("Format", formatLabel(match.matchFormat))
                        MatchDetailRow("Date", match.matchDate?.take(10) ?: "TBD")
                        MatchDetailRow("Time Slot", match.timeSlot.replace("MIN", "").plus(" min"))
                        MatchDetailRow("Type", match.requestType.replace("_", " ").replaceFirstChar { it.uppercase() })
                        MatchDetailRow("Cost", match.estimatedCost?.let { "৳${it.toInt()}" } ?: match.costMode?.replaceFirstChar { it.uppercase() } ?: "N/A")

                        if (match.venue != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Venue", style = MaterialTheme.typography.labelLarge, color = Ink3)
                            Text(match.venue.name, style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Medium)
                            match.venue.area?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Ink3) }
                        } else if (match.venueManual != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Venue", style = MaterialTheme.typography.labelLarge, color = Ink3)
                            Text(match.venueManual, style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MatchDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Ink3)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Medium)
    }
}
