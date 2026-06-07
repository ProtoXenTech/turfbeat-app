package turfbeat.com.ui.screens.clubs

import androidx.compose.foundation.ExperimentalFoundationApi
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
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.data.model.ClubDto
import turfbeat.com.data.model.ClubMemberDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.ClubViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDetailScreen(
    clubId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: ClubViewModel = koinViewModel()
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(clubId) { viewModel.loadClubDetail(clubId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.club?.name ?: "Club", fontWeight = FontWeight.Bold, color = Ink) },
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
                    Text(state.error!!, color = Error, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadClubDetail(clubId) }, colors = ButtonDefaults.buttonColors(containerColor = Pitch)) {
                        Text("Retry", color = Forest)
                    }
                }
            }
        } else if (state.club != null) {
            ClubDetailContent(
                club = state.club!!,
                members = state.members,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ClubDetailContent(
    club: ClubDto,
    members: List<ClubMemberDto>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val tabs = listOf("Overview", "Squad (${club.memberCount})", "Matches")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column(modifier = modifier.fillMaxSize().background(Bg)) {
        ClubHeader(club = club)

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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> OverviewTab(club)
                1 -> SquadTab(members)
                2 -> MatchesTab()
            }
        }
    }
}

@Composable
private fun ClubHeader(club: ClubDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (club.logoUrl != null) {
                AsyncImage(
                    model = club.logoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(PitchSoft)
                )
            } else {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(PitchSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏟️", fontSize = 36.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(club.name, style = MaterialTheme.typography.headlineMedium, color = Ink, fontWeight = FontWeight.Bold)

            Text(
                "${club.area ?: ""}${if (club.area != null && club.district != null) ", " else ""}${club.district ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink3
            )

            if (club.rating != null && club.rating > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", color = Gold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${"%.1f".format(club.rating)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Gold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        " (${club.ratingCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink3
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (club.playStyle != null) {
                    AssistChip(
                        onClick = {},
                        label = { Text(club.playStyle.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = PitchSoft, labelColor = Forest)
                    )
                }
                if (club.matchFormats?.isNotEmpty() == true) {
                    club.matchFormats.forEach { format ->
                        AssistChip(
                            onClick = {},
                            label = { Text(format.replace("_", "-"), style = MaterialTheme.typography.labelSmall) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Panel, labelColor = Ink)
                        )
                    }
                }
            }

            if (club.isLookingForPlayers) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(100.dp), color = PitchSoft) {
                    Text(
                        "Looking for Players",
                        style = MaterialTheme.typography.labelLarge,
                        color = Forest,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(club: ClubDto) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!club.description.isNullOrBlank()) {
            item {
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(club.description, style = MaterialTheme.typography.bodyMedium, color = Ink2)
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Details", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow("Member Count", "${club.memberCount}")
                    DetailRow("Play Style", club.playStyle?.replaceFirstChar { it.uppercase() } ?: "Any")
                    DetailRow("Turf Preference", club.turfPreference?.replaceFirstChar { it.uppercase() } ?: "Any")
                    DetailRow("Age Group", club.ageGroup?.replace("_", "-")?.replaceFirstChar { it.uppercase() } ?: "Mixed")
                    if (club.availableDays?.isNotEmpty() == true) {
                        DetailRow("Available Days", club.availableDays.joinToString(", "))
                    }
                    if (club.availableTime != null) {
                        DetailRow("Available Time", club.availableTime)
                    }
                }
            }
        }

        if (club.neededPositions?.isNotEmpty() == true) {
            item {
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Needed Positions", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            club.neededPositions.forEach { pos ->
                                Surface(shape = RoundedCornerShape(100.dp), color = Hot.copy(alpha = 0.1f)) {
                                    Text(
                                        pos.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Hot,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
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
private fun SquadTab(members: List<ClubMemberDto>) {
    if (members.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👥", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("No members yet", style = MaterialTheme.typography.titleMedium, color = Ink3)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(members, key = { it.id }) { member ->
                MemberCard(member = member)
            }
        }
    }
}

@Composable
private fun MatchesTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📅", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Match history coming soon", style = MaterialTheme.typography.titleMedium, color = Ink3)
        }
    }
}

@Composable
private fun MemberCard(member: ClubMemberDto) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val avatarUrl = member.user?.avatarUrl
            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(PitchSoft)
                )
            } else {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(PitchSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚽", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    member.user?.fullName ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    member.user?.position?.replaceFirstChar { it.uppercase() } ?: "Any",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink3
                )
            }

            if (member.role == "admin") {
                Surface(shape = RoundedCornerShape(100.dp), color = Gold.copy(alpha = 0.15f)) {
                    Text("Admin", style = MaterialTheme.typography.labelSmall, color = Gold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            } else if (member.status == "trial") {
                Surface(shape = RoundedCornerShape(100.dp), color = InfoBg) {
                    Text("Trial", style = MaterialTheme.typography.labelSmall, color = Info, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Ink3)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Medium)
    }
}
