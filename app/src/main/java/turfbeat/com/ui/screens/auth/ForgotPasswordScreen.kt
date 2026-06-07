package turfbeat.com.ui.screens.auth

import androidx.compose.animation.*
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import turfbeat.com.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onPasswordReset: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            when (step) {
                1 -> {
                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Ink,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Enter your email and we'll send you a reset code",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink3,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (email.isNotBlank()) {
                                isLoading = true
                                error = null
                                step = 2
                                isLoading = false
                            } else {
                                error = "Enter your email address"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = email.isNotBlank() && !isLoading,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest, disabledContainerColor = Line)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Forest, strokeWidth = 2.dp)
                        } else {
                            Text("Send Code", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                2 -> {
                    Text(
                        text = "Enter reset code",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Ink,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "We sent a code to $email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink3,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it.filter { c -> c.isDigit() }.take(6) },
                        label = { Text("6-digit code") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center, letterSpacing = 8.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "Hide" else "Show", color = Pitch2)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (otp.length == 6 && newPassword.length >= 6) {
                                isLoading = true
                                error = null
                                onPasswordReset()
                            } else {
                                error = "Enter valid code and password (6+ chars)"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = otp.length == 6 && newPassword.length >= 6 && !isLoading,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest, disabledContainerColor = Line)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Forest, strokeWidth = 2.dp)
                        } else {
                            Text("Reset Password", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ErrorBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error!!,
                        color = Error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
