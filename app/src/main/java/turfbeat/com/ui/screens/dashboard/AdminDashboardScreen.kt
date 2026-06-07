package turfbeat.com.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.ui.screens.matches.MatchStatusBadge
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.AdminDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: AdminDashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.load() }

    val tabs = listOf("Clubs", "Venues", "Players", "Matches")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin", fontWeight = FontWeight.Bold, color = Ink) },
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
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).background(Bg)) {
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Surface,
                    contentColor = Pitch
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    title,
                                    color = if (pagerState.currentPage == index) Pitch else Ink3,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (page) {
                            0 -> {
                                item {
                                    Text("${state.clubs.size} clubs", style = MaterialTheme.typography.labelLarge, color = Ink3)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                items(state.clubs, key = { it.id }) { club ->
                                    AdminClubCard(club = club, onDelete = { viewModel.deleteClub(club.id) })
                                }
                            }
                            1 -> {
                                item {
                                    Text("${state.pendingVenues.size} pending venues", style = MaterialTheme.typography.labelLarge, color = Ink3)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                items(state.pendingVenues, key = { it.id }) { venue ->
                                    AdminVenueCard(
                                        venue = venue,
                                        onApprove = { viewModel.approveVenue(venue.id) },
                                        onReject = { viewModel.deleteVenue(venue.id) }
                                    )
                                }
                            }
                            2 -> {
                                item {
                                    Text("${state.players.size} players", style = MaterialTheme.typography.labelLarge, color = Ink3)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                items(state.players, key = { it.id }) { player ->
                                    AdminPlayerCard(player = player, onDelete = { viewModel.deleteClub(player.id) })
                                }
                            }
                            3 -> {
                                item {
                                    Text("${state.matches.size} matches", style = MaterialTheme.typography.labelLarge, color = Ink3)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                items(state.matches, key = { it.id }) { match ->
                                    AdminMatchCard(match = match, onDelete = { viewModel.deleteMatch(match.id) })
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
private fun AdminClubCard(
    club: turfbeat.com.data.model.ClubDto,
    onDelete: () -> Unit
) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PitchSoft), contentAlignment = Alignment.Center) { Text("🏟️", fontSize = 18.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(club.name, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                club.area?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Ink3) }
            }
            Text("👥 ${club.memberCount}", style = MaterialTheme.typography.bodySmall, color = Ink3)
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onDelete) { Text("Delete", color = Hot) }
        }
    }
}

@Composable
private fun AdminVenueCard(
    venue: turfbeat.com.data.model.VenueDto,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).let {
                if (venue.imageUrl != null) it else it
            }, contentAlignment = Alignment.Center) { Text("📍", fontSize = 18.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(venue.name, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                venue.area?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Ink3) }
            }
            Surface(shape = RoundedCornerShape(100.dp), color = WarningBg) { Text("Pending", style = MaterialTheme.typography.labelSmall, color = Gold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onApprove) { Text("✓", color = Forest, fontWeight = FontWeight.Bold) }
            TextButton(onClick = onReject) { Text("✗", color = Hot) }
        }
    }
}

@Composable
private fun AdminPlayerCard(
    player: turfbeat.com.data.model.UserDto,
    onDelete: () -> Unit
) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PitchSoft), contentAlignment = Alignment.Center) { Text("⚽", fontSize = 18.sp) }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(player.fullName ?: "Unknown", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                player.position?.let { Text(it.replaceFirstChar { c -> c.uppercase() }, style = MaterialTheme.typography.bodySmall, color = Ink3) }
            }
            TextButton(onClick = onDelete) { Text("Delete", color = Hot) }
        }
    }
}

@Composable
private fun AdminMatchCard(
    match: turfbeat.com.data.model.MatchDto,
    onDelete: () -> Unit
) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${match.homeClub?.name ?: "?"} vs ${match.awayClub?.name ?: "?"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${match.matchDate?.take(10) ?: ""} · ${match.matchFormat.replace("_", "-")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink3
                )
            }
            MatchStatusBadge(status = match.status)
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onDelete) { Text("Delete", color = Hot) }
        }
    }
}
