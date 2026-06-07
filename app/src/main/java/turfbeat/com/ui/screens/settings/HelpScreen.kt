package turfbeat.com.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import turfbeat.com.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center", fontWeight = FontWeight.Bold, color = Ink) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Bg).verticalScroll(rememberScrollState())
        ) {
            FAQItem(
                question = "How do I create a club?",
                answer = "Go to Club Dashboard from the home screen and tap 'Create Club'. Fill in your club details and submit. You'll be the first admin."
            )
            FAQItem(
                question = "How do I join a club?",
                answer = "Browse the Club Directory, find a club you like, and tap 'Join'. The club admins will review your request."
            )
            FAQItem(
                question = "How do I create a match?",
                answer = "From the Club Dashboard, go to the Matches tab and tap 'Create Match'. Choose your format, date, venue, and opponent."
            )
            FAQItem(
                question = "How do club ratings work?",
                answer = "After a match, both clubs rate each other on sportsmanship, punctuality, communication, organization, and competitiveness."
            )
            FAQItem(
                question = "How do I use match chat?",
                answer = "Once a match is confirmed, club admins can chat in real-time using the Match Chat feature. Tap the chat icon on the match detail screen."
            )
            FAQItem(
                question = "What is the approval process?",
                answer = "New clubs start as 'Draft'. Once you have 5+ members, complete info, and 2 admins, you can submit for approval."
            )
        }
    }
}

@Composable
private fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(question, style = MaterialTheme.typography.bodyLarge, color = Ink, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Text(if (expanded) "▾" else "▸", color = Ink3)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(answer, style = MaterialTheme.typography.bodyMedium, color = Ink3)
            }
        }
    }
}
