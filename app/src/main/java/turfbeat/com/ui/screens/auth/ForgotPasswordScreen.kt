package turfbeat.com.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import turfbeat.com.ui.theme.*
import turfbeat.com.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onPasswordReset: () -> Unit
) {
    val authViewModel: AuthViewModel = koinViewModel()
    val uiState by authViewModel.uiState.collectAsState()
    var step by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn == true) onPasswordReset()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Bg).padding(padding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            when (step) {
                1 -> {
                    Text("Forgot password?", style = MaterialTheme.typography.headlineMedium, color = Ink, modifier = Modifier.fillMaxWidth())
                    Text("Enter your email and we'll send you a reset code", style = MaterialTheme.typography.bodyMedium, color = Ink3, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch))

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isNotBlank()) {
                                authViewModel.sendForgotOtp(email)
                                step = 2
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = email.isNotBlank() && !uiState.isLoading,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest, disabledContainerColor = Line)
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Forest, strokeWidth = 2.dp)
                        else Text("Send Code", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }

                2 -> {
                    Text("Enter reset code", style = MaterialTheme.typography.headlineMedium, color = Ink, modifier = Modifier.fillMaxWidth())
                    Text("We sent a code to $email", style = MaterialTheme.typography.bodyMedium, color = Ink3, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

                    OutlinedTextField(value = otp, onValueChange = { otp = it.filter { c -> c.isDigit() }.take(6) }, label = { Text("6-digit code") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), shape = RoundedCornerShape(12.dp), textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center, letterSpacing = 8.sp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch))

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done), shape = RoundedCornerShape(12.dp), trailingIcon = { TextButton(onClick = { passwordVisible = !passwordVisible }) { Text(if (passwordVisible) "Hide" else "Show", color = Pitch2) } }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch))

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { authViewModel.resetPassword(email, otp, newPassword) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = otp.length == 6 && newPassword.length >= 6 && !uiState.isLoading,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest, disabledContainerColor = Line)
                    ) {
                        if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Forest, strokeWidth = 2.dp)
                        else Text("Reset Password", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = ErrorBg), shape = RoundedCornerShape(12.dp)) {
                    Text(uiState.error!!, color = Error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
                }
            }
        }
    }
}
