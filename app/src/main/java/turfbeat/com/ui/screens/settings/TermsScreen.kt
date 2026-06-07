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
fun TermsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Service", fontWeight = FontWeight.Bold, color = Ink) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Bg).verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Terms of Service", style = MaterialTheme.typography.headlineMedium, color = Ink, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "1. Acceptance of Terms\n\n" +
                        "By using TurfBeat, you agree to these terms of service.\n\n" +
                        "2. User Accounts\n\n" +
                        "You are responsible for maintaining your account and providing accurate information.\n\n" +
                        "3. Club Management\n\n" +
                        "Club admins are responsible for their club's conduct and member management.\n\n" +
                        "4. Match Conduct\n\n" +
                        "Users are expected to maintain good sportsmanship. Reports of misconduct may result in account suspension.\n\n" +
                        "5. Privacy\n\n" +
                        "Your data is handled according to our Privacy Policy.\n\n" +
                        "6. Modifications\n\n" +
                        "We reserve the right to modify these terms with notice to users.\n\n" +
                        "Last updated: June 2026",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink2
                    )
                }
            }
        }
    }
}
