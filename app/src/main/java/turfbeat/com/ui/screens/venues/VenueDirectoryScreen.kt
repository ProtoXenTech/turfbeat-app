package turfbeat.com.ui.screens.venues

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.data.model.VenueDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.VenueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueDirectoryScreen(
    onNavigateBack: () -> Unit,
    onVenueClick: (Int) -> Unit
) {
    val viewModel: VenueViewModel = koinViewModel()
    val state by viewModel.listState.collectAsState()
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadVenues() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Venues", fontWeight = FontWeight.Bold, color = Ink) },
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
            modifier = Modifier.fillMaxSize().background(Bg).padding(padding)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it; viewModel.searchVenues(it) },
                placeholder = { Text("Search venues...", color = Ink3) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Pitch, cursorColor = Pitch,
                    focusedContainerColor = Surface, unfocusedContainerColor = Surface
                ),
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        TextButton(onClick = { searchText = ""; viewModel.searchVenues("") }) {
                            Text("Clear", color = Ink3)
                        }
                    }
                }
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Pitch)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Something went wrong", color = Ink3)
                        TextButton(onClick = { viewModel.loadVenues() }) { Text("Retry", color = Pitch2) }
                    }
                }
            } else if (state.venues.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No venues found", style = MaterialTheme.typography.titleMedium, color = Ink3)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.venues, key = { it.id }) { venue ->
                        VenueCard(venue = venue, onClick = { onVenueClick(venue.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun VenueCard(venue: VenueDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (venue.imageUrl != null) {
                AsyncImage(
                    model = venue.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Panel)
                )
            } else {
                Box(
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(PitchSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📍", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(venue.name, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                Text(
                    venue.area ?: venue.district ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (venue.contact != null) {
                    Text(venue.contact, style = MaterialTheme.typography.bodySmall, color = Ink3)
                }
                if (venue.rating != null && venue.rating > 0) {
                    Text("★ ${"%.1f".format(venue.rating)}", style = MaterialTheme.typography.bodySmall, color = Gold)
                }
            }
        }
    }
}
