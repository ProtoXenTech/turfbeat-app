package turfbeat.com.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.data.model.MatchDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.MatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchBoardScreen(
    onNavigateBack: () -> Unit,
    onMatchClick: (Int) -> Unit
) {
    val viewModel: MatchViewModel = koinViewModel()
    val state by viewModel.listState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.loadMatches() }

    val tabs = listOf("Upcoming", "Open Listings", "Completed")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matches", fontWeight = FontWeight.Bold, color = Ink) },
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

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Pitch)
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val matches = when (page) {
                        0 -> state.upcoming
                        1 -> state.openListings
                        2 -> state.completed
                        else -> emptyList()
                    }

                    if (matches.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (page == 1) "📋" else "📅", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    when (page) {
                                        0 -> "No upcoming matches"
                                        1 -> "No open listings"
                                        else -> "No completed matches"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Ink3
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(matches, key = { it.id }) { match ->
                                MatchCard(match = match, onClick = { onMatchClick(match.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: MatchDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        match.homeClub?.name ?: "TBD",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Text("vs", style = MaterialTheme.typography.bodyLarge, color = Ink3, modifier = Modifier.padding(horizontal = 12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        match.awayClub?.name ?: "Open",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (match.awayClub != null) Ink else Pitch2,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatLabel(match.matchFormat), style = MaterialTheme.typography.bodySmall, color = Ink3)
                    if (match.matchDate != null) {
                        Text(" · ", style = MaterialTheme.typography.bodySmall, color = Ink3)
                        Text(formatDate(match.matchDate), style = MaterialTheme.typography.bodySmall, color = Ink3)
                    }
                }
                MatchStatusBadge(status = match.status)
            }

            if (match.venue != null || match.venueManual != null) {
                Text(
                    "📍 ${match.venue?.name ?: match.venueManual}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink3,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (match.homeGoals != null && match.awayGoals != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Forest
                ) {
                    Text(
                        "${match.homeGoals} - ${match.awayGoals}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Surface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MatchStatusBadge(status: String) {
    val (label, bg) = when (status) {
        "confirmed" -> "Confirmed" to PitchSoft
        "completed" -> "Completed" to Forest.copy(alpha = 0.15f)
        "cancelled" -> "Cancelled" to ErrorBg
        "rejected" -> "Rejected" to ErrorBg
        else -> "Pending" to Gold.copy(alpha = 0.15f)
    }
    val textColor = when (status) {
        "confirmed" -> Forest
        "completed" -> Forest
        "cancelled" -> Error
        "rejected" -> Error
        else -> Gold
    }
    Surface(shape = RoundedCornerShape(100.dp), color = bg) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

internal fun formatLabel(format: String): String = when (format) {
    "five_a_side" -> "5-a-side"
    "seven_a_side" -> "7-a-side"
    "nine_a_side" -> "9-a-side"
    "eleven_a_side" -> "11-a-side"
    "futsal" -> "Futsal"
    else -> format
}

private fun formatDate(dateStr: String): String = dateStr.take(10)
