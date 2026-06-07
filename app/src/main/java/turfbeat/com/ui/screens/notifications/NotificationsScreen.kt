package turfbeat.com.ui.screens.notifications

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
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.data.model.NotificationDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: NotificationViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startPolling()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, color = Ink) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllRead() }) {
                            Text("Mark all read", color = Pitch2)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        if (state.notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(Bg), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No notifications", style = MaterialTheme.typography.titleMedium, color = Ink3)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(Bg),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.notifications, key = { it.id }) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = { viewModel.markRead(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Surface else PitchSoft.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (notification.isRead) Panel else PitchSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(notificationIcon(notification.type), fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Pitch))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(notification.message, style = MaterialTheme.typography.bodySmall, color = Ink3)
                notification.createdAt?.let {
                    Text(
                        it.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink3,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun notificationIcon(type: String): String = when {
    type.contains("join_request") -> "👋"
    type.contains("match") -> "⚽"
    type.contains("rating") -> "⭐"
    type.contains("approval") -> "✅"
    type.contains("invite") -> "📨"
    type.contains("interest") -> "👀"
    type.contains("player") -> "👤"
    type.contains("club") -> "🏟️"
    else -> "🔔"
}
