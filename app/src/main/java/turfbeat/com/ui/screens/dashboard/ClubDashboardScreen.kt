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
import turfbeat.com.data.model.ClubDashboardDto
import turfbeat.com.data.model.ClubJoinRequestDto
import turfbeat.com.data.model.ClubMemberDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDashboardScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: ClubDashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.load() }

    val tabs = listOf("Overview", "Squad", "Requests")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.dashboard?.club?.name ?: "Club Admin", fontWeight = FontWeight.Bold, color = Ink) },
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
        } else if (state.error != null && state.dashboard == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Failed to load", color = Ink3)
                    TextButton(onClick = { viewModel.load() }) { Text("Retry", color = Pitch2) }
                }
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
                    val d = state.dashboard
                    when (page) {
                        0 -> OverviewTab(d, viewModel)
                        1 -> SquadTab(d?.members ?: emptyList(), viewModel)
                        2 -> RequestsTab(d?.joinRequests ?: emptyList(), viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(dashboard: ClubDashboardDto?, viewModel: ClubDashboardViewModel) {
    val club = dashboard?.club

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(CircleShape).background(PitchSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("🏟️", fontSize = 24.sp) }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(club?.name ?: "My Club", style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
                            club?.area?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = Ink3) }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${dashboard?.members?.size ?: 0}", style = MaterialTheme.typography.titleLarge, color = Ink, fontWeight = FontWeight.Bold)
                            Text("Members", style = MaterialTheme.typography.bodySmall, color = Ink3)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${club?.rating ?: "—"}", style = MaterialTheme.typography.titleLarge, color = Gold, fontWeight = FontWeight.Bold)
                            Text("Rating", style = MaterialTheme.typography.bodySmall, color = Ink3)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${dashboard?.joinRequests?.size ?: 0}", style = MaterialTheme.typography.titleLarge, color = Hot, fontWeight = FontWeight.Bold)
                            Text("Requests", style = MaterialTheme.typography.bodySmall, color = Ink3)
                        }
                    }
                }
            }
        }

        club?.let {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Club Details", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row2("Play Style", it.playStyle?.replaceFirstChar { c -> c.uppercase() } ?: "Any")
                        Row2("Match Formats", it.matchFormats?.joinToString(", ") { f -> f.replace("_", "-") } ?: "Any")
                        Row2("Members", "${dashboard?.members?.size ?: 0}")
                        Row2("Approval", when (it.approvalStatus) {
                            "approved" -> "✅ Approved"
                            "pending" -> "⏳ Pending Review"
                            "rejected" -> "❌ Rejected"
                            else -> "📝 Draft"
                        })
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quick Actions", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (club?.approvalStatus != "approved") {
                        Button(
                            onClick = { viewModel.submitApproval() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Pitch)
                        ) {
                            Text("Submit for Approval", color = Forest, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(100.dp), color = PitchSoft) {
                            Text(
                                "Approved",
                                style = MaterialTheme.typography.labelLarge,
                                color = Forest,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun SquadTab(members: List<ClubMemberDto>, viewModel: ClubDashboardViewModel) {
    if (members.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No members yet", style = MaterialTheme.typography.titleMedium, color = Ink3)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(members, key = { it.id }) { member ->
                var showMenu by remember { mutableStateOf(false) }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(PitchSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("⚽", fontSize = 20.sp) }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(member.user?.fullName ?: "Unknown", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                            Row {
                                member.user?.position?.let { Text(it.replaceFirstChar { c -> c.uppercase() }, style = MaterialTheme.typography.bodySmall, color = Ink3) }
                                member.user?.skillLevel?.let { Text(" · ${it.replaceFirstChar { c -> c.uppercase() }}", style = MaterialTheme.typography.bodySmall, color = Ink3) }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (member.role == "admin") {
                                Surface(shape = RoundedCornerShape(100.dp), color = Gold.copy(alpha = 0.15f)) {
                                    Text("Admin", style = MaterialTheme.typography.labelSmall, color = Gold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            if (member.status == "trial") {
                                Surface(shape = RoundedCornerShape(100.dp), color = InfoBg) {
                                    Text("Trial", style = MaterialTheme.typography.labelSmall, color = Info, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Box {
                            TextButton(onClick = { showMenu = true }) { Text("···", color = Ink3) }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                if (member.role != "admin") {
                                    DropdownMenuItem(text = { Text("Promote to Admin") }, onClick = {
                                        viewModel.updateMember(member.id, role = "admin")
                                        showMenu = false
                                    })
                                }
                                DropdownMenuItem(text = { Text(if (member.status == "trial") "Set Active" else "Set Trial") }, onClick = {
                                    viewModel.updateMember(member.id, status = if (member.status == "trial") "active" else "trial")
                                    showMenu = false
                                })
                                DropdownMenuItem(text = { Text("Remove", color = Hot) }, onClick = {
                                    viewModel.removeMember(member.id)
                                    showMenu = false
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestsTab(requests: List<ClubJoinRequestDto>, viewModel: ClubDashboardViewModel) {
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📬", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("No pending requests", style = MaterialTheme.typography.titleMedium, color = Ink3)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(requests.filter { it.status == "pending" }, key = { it.id }) { request ->
                Card(
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
                        ) { Text("⚽", fontSize = 20.sp) }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(request.user?.fullName ?: "Unknown", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                            request.user?.position?.let { Text(it.replaceFirstChar { c -> c.uppercase() }, style = MaterialTheme.typography.bodySmall, color = Ink3) }
                        }

                        TextButton(onClick = { viewModel.handleJoinRequest(request.id, true) }) {
                            Text("Accept", color = Forest, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { viewModel.handleJoinRequest(request.id, false) }) {
                            Text("Decline", color = Hot)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Row2(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Ink3)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Ink, fontWeight = FontWeight.Medium)
    }
}
