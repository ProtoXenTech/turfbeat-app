package turfbeat.com.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import turfbeat.com.ui.theme.*

@Composable
fun SplashScreen(
    isLoggedIn: Boolean?,
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            delay(1500)
            if (isLoggedIn) onNavigateToHome() else onNavigateToSignIn()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            Text(
                text = "⚽",
                style = MaterialTheme.typography.displayLarge,
                fontSize = MaterialTheme.typography.displayLarge.fontSize * 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TurfBeat",
                style = MaterialTheme.typography.displayLarge,
                color = Forest,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Bangladesh's Football Community",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink3,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
