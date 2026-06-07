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
fun ContactScreen(onNavigateBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Us", fontWeight = FontWeight.Bold, color = Ink) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).background(Bg).verticalScroll(rememberScrollState())
        ) {
            if (submitted) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PitchSoft)
                ) {
                    Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                        Text("✅", style = MaterialTheme.typography.headlineLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Message sent!", style = MaterialTheme.typography.titleLarge, color = Forest, fontWeight = FontWeight.Bold)
                        Text("We'll get back to you as soon as possible.", style = MaterialTheme.typography.bodyMedium, color = Forest)
                    }
                }
            } else {
                Text(
                    "Have a question or feedback? We'd love to hear from you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink3,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { submitted = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                    enabled = name.isNotBlank() && email.isNotBlank() && message.isNotBlank(),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest, disabledContainerColor = Line)
                ) {
                    Text("Send Message", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Other ways to reach us", style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📧 support@turfbeat.com", style = MaterialTheme.typography.bodyMedium, color = Ink2)
                    Text("🌐 turfbeat.com", style = MaterialTheme.typography.bodyMedium, color = Ink2)
                }
            }
        }
    }
}
