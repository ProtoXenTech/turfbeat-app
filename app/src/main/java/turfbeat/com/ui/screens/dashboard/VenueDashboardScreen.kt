package turfbeat.com.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.data.model.VenueDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.VenueDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueDashboardScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: VenueDashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Venues", fontWeight = FontWeight.Bold, color = Ink) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Bg)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Pitch)
                }
            } else if (state.venues.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No venues yet", style = MaterialTheme.typography.titleMedium, color = Ink3)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.venues, key = { it.id }) { venue ->
                        VenueOwnerCard(venue = venue, onDelete = { viewModel.deleteVenue(venue.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun VenueOwnerCard(venue: VenueDto, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).let {
                    if (venue.imageUrl != null) it else it
                },
                contentAlignment = Alignment.Center
            ) {
                Text("📍", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(venue.name, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                venue.area?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Ink3) }
                if (venue.rating != null && venue.rating > 0) {
                    Text("★ ${"%.1f".format(venue.rating)}", style = MaterialTheme.typography.bodySmall, color = Gold)
                }
            }

            TextButton(onClick = onDelete) {
                Text("Delete", color = Hot)
            }
        }
    }
}
