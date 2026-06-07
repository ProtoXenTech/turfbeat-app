package turfbeat.com.ui.screens.clubs

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
import turfbeat.com.data.model.ClubDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.ClubViewModel
import turfbeat.com.util.Locations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDirectoryScreen(
    onNavigateBack: () -> Unit,
    onClubClick: (Int) -> Unit
) {
    val viewModel: ClubViewModel = koinViewModel()
    val state by viewModel.listState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showAreaFilter by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadClubs() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clubs", fontWeight = FontWeight.Bold, color = Ink) },
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
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchClubs(it)
                },
                placeholder = { Text("Search clubs...", color = Ink3) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Pitch,
                    cursorColor = Pitch,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                ),
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        TextButton(onClick = { searchText = ""; viewModel.searchClubs("") }) {
                            Text("Clear", color = Ink3)
                        }
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedArea != null,
                    onClick = { showAreaFilter = true },
                    label = { Text(state.selectedArea ?: "All Areas") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PitchSoft,
                        selectedLabelColor = Forest
                    )
                )
                FilterChip(
                    selected = state.recruitingOnly,
                    onClick = { viewModel.toggleRecruiting() },
                    label = { Text("Recruiting") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PitchSoft,
                        selectedLabelColor = Forest
                    )
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Pitch)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Something went wrong", color = Ink3)
                        Text(state.error!!, color = Error, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadClubs() }, colors = ButtonDefaults.buttonColors(containerColor = Pitch)) {
                            Text("Retry", color = Forest)
                        }
                    }
                }
            } else if (state.clubs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏟️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No clubs found", style = MaterialTheme.typography.titleMedium, color = Ink3)
                        Text("Try adjusting your search or filters", style = MaterialTheme.typography.bodySmall, color = Ink3)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.clubs, key = { it.id }) { club ->
                        ClubCard(club = club, onClick = { onClubClick(club.id) })
                    }
                }
            }
        }
    }

    if (showAreaFilter) {
        AlertDialog(
            onDismissRequest = { showAreaFilter = false },
            title = { Text("Select Area") },
            text = {
                Column {
                    Locations.areas.forEach { (division, areaList) ->
                        Text(division, style = MaterialTheme.typography.labelLarge, color = Forest, fontWeight = FontWeight.Bold)
                        areaList.forEach { area ->
                            TextButton(onClick = {
                                viewModel.filterByArea(area)
                                showAreaFilter = false
                            }) {
                                Text(area, color = Ink)
                            }
                        }
                    }
                    TextButton(onClick = {
                        viewModel.filterByArea(null)
                        showAreaFilter = false
                    }) {
                        Text("Show All", color = Pitch2, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun ClubCard(club: ClubDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (club.logoUrl != null) {
                AsyncImage(
                    model = club.logoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PitchSoft)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PitchSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏟️", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = club.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (club.isLookingForPlayers) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = PitchSoft
                        ) {
                            Text("Recruiting", style = MaterialTheme.typography.labelSmall, color = Forest, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = club.area ?: club.district ?: club.division ?: "Bangladesh",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink3
                    )
                    if (club.memberCount > 0) {
                        Text(" · ", style = MaterialTheme.typography.bodySmall, color = Ink3)
                        Text("${club.memberCount} members", style = MaterialTheme.typography.bodySmall, color = Ink3)
                    }
                    if (club.rating != null && club.rating > 0) {
                        Text(" · ", style = MaterialTheme.typography.bodySmall, color = Ink3)
                        Text("★ ${"%.1f".format(club.rating)}", style = MaterialTheme.typography.bodySmall, color = Gold)
                    }
                }
            }
        }
    }
}
