package turfbeat.com.ui.screens.venues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import turfbeat.com.ui.viewmodel.VenueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueDetailScreen(
    venueId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: VenueViewModel = koinViewModel()
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(venueId) { viewModel.loadVenueDetail(venueId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.venue?.name ?: "Venue", fontWeight = FontWeight.Bold, color = Ink) },
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
                    TextButton(onClick = { viewModel.loadVenueDetail(venueId) }) { Text("Retry", color = Pitch2) }
                }
            }
        } else if (state.venue != null) {
            val venue = state.venue!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Bg)
                    .verticalScroll(rememberScrollState())
            ) {
                if (venue.images.isNotEmpty()) {
                    LazyRow(modifier = Modifier.height(240.dp)) {
                        items(venue.images) { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(360.dp)
                                    .fillMaxHeight()
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Panel)
                            )
                        }
                    }
                } else if (venue.imageUrl != null) {
                    AsyncImage(
                        model = venue.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
                            .background(Panel)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(PitchSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📍", fontSize = 48.sp)
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(venue.name, style = MaterialTheme.typography.headlineMedium, color = Ink, fontWeight = FontWeight.Bold)
                    Text(
                        venue.area?.let { "$it${if (venue.district != null) ", ${venue.district}" else ""}" } ?: venue.district ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink3
                    )

                    if (venue.rating != null && venue.rating > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("★", color = Gold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${"%.1f".format(venue.rating)}", style = MaterialTheme.typography.titleLarge, color = Gold, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Details", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            if (venue.contact != null) {
                                DetailRow2("Contact", venue.contact)
                            }
                            if (venue.area != null) DetailRow2("Area", venue.area)
                            if (venue.district != null) DetailRow2("District", venue.district)
                            if (venue.division != null) DetailRow2("Division", venue.division)

                            if (venue.availableSlots?.isNotEmpty() == true) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Available Slots", style = MaterialTheme.typography.labelLarge, color = Ink3)
                                venue.availableSlots.forEach { slot ->
                                    Text("- $slot", style = MaterialTheme.typography.bodySmall, color = Ink2)
                                }
                            }
                        }
                    }

                    if (venue.mapLink != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(PitchSoft),
                                    contentAlignment = Alignment.Center
                                ) { Text("🗺️", fontSize = 20.sp) }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("View on Map", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                                    Text(venue.mapLink, style = MaterialTheme.typography.bodySmall, color = Ink3, maxLines = 1)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow2(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Ink3)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Medium)
    }
}
