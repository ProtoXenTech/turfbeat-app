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
fun PrivacyScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold, color = Ink) },
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
                    Text("Privacy Policy", style = MaterialTheme.typography.headlineMedium, color = Ink, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "1. Information We Collect\n\n" +
                        "We collect information you provide: name, email, phone number, location, and profile details.\n\n" +
                        "2. How We Use Information\n\n" +
                        "We use your information to provide matchmaking, club management, and venue booking services.\n\n" +
                        "3. Data Sharing\n\n" +
                        "Your profile information is visible to other users for matchmaking purposes. We do not sell your data.\n\n" +
                        "4. Data Security\n\n" +
                        "We implement industry-standard security measures to protect your data.\n\n" +
                        "5. Your Rights\n\n" +
                        "You can update or delete your account data at any time through your profile settings.\n\n" +
                        "6. Contact\n\n" +
                        "For privacy concerns, contact us at support@turfbeat.com\n\n" +
                        "Last updated: June 2026",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink2
                    )
                }
            }
        }
    }
}
