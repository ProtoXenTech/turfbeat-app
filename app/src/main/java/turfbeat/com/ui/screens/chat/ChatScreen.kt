package turfbeat.com.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import turfbeat.com.data.model.ChatMessageDto
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    matchId: Int,
    onNavigateBack: () -> Unit
) {
    val viewModel: ChatViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(matchId) {
        viewModel.connect(matchId)
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(state.messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Match Chat", fontWeight = FontWeight.Bold, color = Ink)
                        if (state.connected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Pitch))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 0.dp,
                color = Surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type a message...", color = Ink3) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Pitch,
                            cursorColor = Pitch,
                            focusedContainerColor = Bg,
                            unfocusedContainerColor = Bg
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledIconButton(
                        onClick = {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        },
                        enabled = inputText.isNotBlank(),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Pitch)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Forest)
                    }
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Pitch)
            }
        } else if (state.messages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💬", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No messages yet", style = MaterialTheme.typography.titleMedium, color = Ink3)
                    Text("Start the conversation", style = MaterialTheme.typography.bodySmall, color = Ink3)
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(padding).background(Bg),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg, isMine = false)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageDto, isMine: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMine) 18.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 18.dp
            ),
            color = if (isMine) Pitch else Surface
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                message.user?.let { user ->
                    Text(
                        user.fullName ?: "Unknown",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isMine) Forest else Pitch2,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    message.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMine) Forest else Ink
                )
                Text(
                    message.createdAt?.takeLast(5)?.take(5) ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMine) Forest.copy(alpha = 0.6f) else Ink3,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
