package turfbeat.com.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import turfbeat.com.ui.theme.*

data class QuickAction(
    val title: String,
    val subtitle: String,
    val icon: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String?,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val quickActions = listOf(
        QuickAction("Find Clubs", "Discover clubs near you", "🏟️", "clubs"),
        QuickAction("Find Players", "Scout available players", "⚽", "players"),
        QuickAction("Open Matches", "Find opponents", "📅", "matches"),
        QuickAction("Venues", "Browse turfs & grounds", "📍", "venues")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TurfBeat", fontWeight = FontWeight.Bold, color = Forest) },
                actions = {
                    TextButton(onClick = { onNavigate("settings") }) {
                        Text("⚙️", fontSize = 18.sp)
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PitchSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚽", fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userName ?: "Player",
                            style = MaterialTheme.typography.titleLarge,
                            color = Ink
                        )
                        Text(
                            text = "Bangladesh's Football Community",
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink3
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    action = quickActions[0],
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("clubs") }
                )
                QuickActionCard(
                    action = quickActions[1],
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("players") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    action = quickActions[2],
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("matches") }
                )
                QuickActionCard(
                    action = quickActions[3],
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("venues") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Dashboard",
                style = MaterialTheme.typography.titleMedium,
                color = Ink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigate("player_dashboard") },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Forest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Player Profile", style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text("Manage your clubs & matches", style = MaterialTheme.typography.bodySmall, color = Ink3)
                    }
                    Text("→", color = Ink3)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigate("club_dashboard") },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PitchSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏟️", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Club Dashboard", style = MaterialTheme.typography.titleMedium, color = Ink)
                        Text("Manage squad, matches & requests", style = MaterialTheme.typography.bodySmall, color = Ink3)
                    }
                    Text("→", color = Ink3)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PitchSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(action.icon, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleMedium,
                color = Ink,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = action.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Ink3,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
