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
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.data.model.ClubDto
import turfbeat.com.data.model.MatchDto
import turfbeat.com.ui.screens.matches.MatchCard
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.PlayerDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDashboardScreen(
    onNavigateBack: () -> Unit,
    onClubClick: (Int) -> Unit,
    onMatchClick: (Int) -> Unit
) {
    val viewModel: PlayerDashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.load() }

    val tabs = listOf("Clubs", "Matches")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Dashboard", fontWeight = FontWeight.Bold, color = Ink) },
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
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(Bg)
            ) {
                item {
                    ProfileCard(profile = state.profile)
                }

                item {
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
                }

                item {
                    when (pagerState.currentPage) {
                        0 -> ClubsSection(
                            clubs = state.myClubs,
                            onClubClick = onClubClick,
                            onLeaveClub = { viewModel.leaveClub(it) }
                        )
                        1 -> MatchesSection(
                            matches = state.myMatches,
                            onMatchClick = onMatchClick
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun ProfileCard(profile: turfbeat.com.data.model.UserDto?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (profile?.avatarUrl != null) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(PitchSoft)
                )
            } else {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(PitchSoft),
                    contentAlignment = Alignment.Center
                ) { Text("⚽", fontSize = 28.sp) }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(profile?.fullName ?: "Player", style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
                Row {
                    profile?.position?.let { Text(it.replaceFirstChar { c -> c.uppercase() }, style = MaterialTheme.typography.bodyMedium, color = Ink3) }
                    profile?.skillLevel?.let { Text(" · ${it.replaceFirstChar { c -> c.uppercase() }}", style = MaterialTheme.typography.bodyMedium, color = Ink3) }
                }
                profile?.area?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Ink3) }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Clubs", "${profile?.let { 1 } ?: 0}")
            StatItem("Matches", "${profile?.let { 0 } ?: 0}")
            StatItem("Rating", "—")
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Ink3)
    }
}

@Composable
private fun ClubsSection(
    clubs: List<ClubDto>,
    onClubClick: (Int) -> Unit,
    onLeaveClub: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("My Clubs", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (clubs.isEmpty()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏟️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Not in any club yet", style = MaterialTheme.typography.bodyMedium, color = Ink3)
                    Text("Browse clubs and send a join request", style = MaterialTheme.typography.bodySmall, color = Ink3)
                }
            }
        } else {
            clubs.forEach { club ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(PitchSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("🏟️", fontSize = 20.sp) }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(club.name, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                            club.area?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Ink3) }
                        }

                        TextButton(onClick = { onLeaveClub(club.id) }) {
                            Text("Leave", color = Hot)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchesSection(
    matches: List<MatchDto>,
    onMatchClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("My Matches", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (matches.isEmpty()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📅", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No matches yet", style = MaterialTheme.typography.bodyMedium, color = Ink3)
                    Text("Join a club to start playing", style = MaterialTheme.typography.bodySmall, color = Ink3)
                }
            }
        } else {
            matches.forEach { match ->
                MatchCard(match = match, onClick = { onMatchClick(match.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
