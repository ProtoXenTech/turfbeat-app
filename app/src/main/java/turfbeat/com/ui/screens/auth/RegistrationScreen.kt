package turfbeat.com.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.unit.sp
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
import turfbeat.com.ui.theme.*
import turfbeat.com.util.Locations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit,
    onRegistrationComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val scrollState = rememberScrollState()

    var phone by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedDivision by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("") }
    var selectedArea by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf("") }
    var selectedSkill by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }

    val stepTitles = listOf("Phone", "Profile", "Account", "Verify")
    val stepProgress = currentStep / 4f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onNavigateBack()
                    }) {
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
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            LinearProgressIndicator(
                progress = { stepProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = Pitch,
                trackColor = Line
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stepTitles.forEachIndexed { index, title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index + 1 <= currentStep) Pitch else Ink3,
                        fontWeight = if (index + 1 == currentStep) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (currentStep) {
                1 -> StepPhone(
                    phone = phone,
                    onPhoneChange = { phone = it },
                    error = error,
                    onNext = {
                        if (phone.length >= 11) {
                            error = null
                            currentStep++
                        } else {
                            error = "Enter a valid phone number"
                        }
                    }
                )

                2 -> StepProfile(
                    fullName = fullName,
                    onNameChange = { fullName = it },
                    selectedDivision = selectedDivision,
                    onDivisionChange = { selectedDivision = it; selectedDistrict = ""; selectedArea = "" },
                    selectedDistrict = selectedDistrict,
                    onDistrictChange = { selectedDistrict = it; selectedArea = "" },
                    selectedArea = selectedArea,
                    onAreaChange = { selectedArea = it },
                    selectedPosition = selectedPosition,
                    onPositionChange = { selectedPosition = it },
                    selectedSkill = selectedSkill,
                    onSkillChange = { selectedSkill = it },
                    error = error,
                    onNext = {
                        if (fullName.isNotBlank() && selectedDivision.isNotBlank() && selectedPosition.isNotBlank() && selectedSkill.isNotBlank()) {
                            error = null
                            currentStep++
                        } else {
                            error = "Please fill in all required fields"
                        }
                    }
                )

                3 -> StepAccount(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    error = error,
                    isLoading = isLoading,
                    onNext = {
                        if (email.isNotBlank() && password.length >= 6) {
                            error = null
                            isLoading = true
                            otpSent = true
                            currentStep++
                            isLoading = false
                        } else {
                            error = "Valid email and password (6+ chars) required"
                        }
                    }
                )

                4 -> StepOtp(
                    otp = otp,
                    onOtpChange = { otp = it },
                    email = email,
                    error = error,
                    isLoading = isLoading,
                    onVerify = {
                        if (otp.length == 6) {
                            isLoading = true
                            error = null
                            onRegistrationComplete()
                        } else {
                            error = "Enter the 6-digit code"
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (error != null) {
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

            Spacer(modifier = Modifier.height(16.dp))

            if (currentStep > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { currentStep-- }) {
                        Text("Back", color = Ink3)
                    }
                    Text(
                        text = "Step $currentStep of 4",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink3
                    )
                }
            }
        }
    }
}

@Composable
private fun StepPhone(
    phone: String,
    onPhoneChange: (String) -> Unit,
    error: String?,
    onNext: () -> Unit
) {
    Column {
        Text(
            text = "What's your phone number?",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink
        )
        Text(
            text = "We'll use this to verify your identity",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink3,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { onPhoneChange(it.filter { c -> c.isDigit() }.take(14)) },
            label = { Text("Phone Number") },
            leadingIcon = { Text("+880", style = MaterialTheme.typography.bodyMedium, color = Ink3) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest)
        ) {
            Text("Continue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepProfile(
    fullName: String,
    onNameChange: (String) -> Unit,
    selectedDivision: String,
    onDivisionChange: (String) -> Unit,
    selectedDistrict: String,
    onDistrictChange: (String) -> Unit,
    selectedArea: String,
    onAreaChange: (String) -> Unit,
    selectedPosition: String,
    onPositionChange: (String) -> Unit,
    selectedSkill: String,
    onSkillChange: (String) -> Unit,
    error: String?,
    onNext: () -> Unit
) {
    var divisionExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var areaExpanded by remember { mutableStateOf(false) }
    var positionExpanded by remember { mutableStateOf(false) }
    var skillExpanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink
        )
        Text(
            text = "Build your player profile",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink3,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = divisionExpanded, onExpandedChange = { divisionExpanded = it }) {
            OutlinedTextField(
                value = selectedDivision,
                onValueChange = {},
                readOnly = true,
                label = { Text("Division") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = divisionExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch)
            )
            ExposedDropdownMenu(expanded = divisionExpanded, onDismissRequest = { divisionExpanded = false }) {
                Locations.divisions.forEach { div ->
                    DropdownMenuItem(
                        text = { Text(div) },
                        onClick = { onDivisionChange(div); divisionExpanded = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedDivision.isNotBlank()) {
            ExposedDropdownMenuBox(expanded = districtExpanded, onExpandedChange = { districtExpanded = it }) {
                OutlinedTextField(
                    value = selectedDistrict,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("District") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch)
                )
                ExposedDropdownMenu(expanded = districtExpanded, onDismissRequest = { districtExpanded = false }) {
                    (Locations.districts[selectedDivision] ?: emptyList()).forEach { dist ->
                        DropdownMenuItem(
                            text = { Text(dist) },
                            onClick = { onDistrictChange(dist); districtExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (selectedDistrict.isNotBlank()) {
            val areaList = Locations.areas[selectedDistrict] ?: Locations.areas.entries.firstOrNull { it.key == selectedDistrict }?.value
            if (areaList != null) {
                ExposedDropdownMenuBox(expanded = areaExpanded, onExpandedChange = { areaExpanded = it }) {
                    OutlinedTextField(
                        value = selectedArea,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Area") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch)
                    )
                    ExposedDropdownMenu(expanded = areaExpanded, onDismissRequest = { areaExpanded = false }) {
                        areaList.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area) },
                                onClick = { onAreaChange(area); areaExpanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        ExposedDropdownMenuBox(expanded = positionExpanded, onExpandedChange = { positionExpanded = it }) {
            OutlinedTextField(
                value = selectedPosition,
                onValueChange = {},
                readOnly = true,
                label = { Text("Position") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = positionExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch)
            )
            ExposedDropdownMenu(expanded = positionExpanded, onDismissRequest = { positionExpanded = false }) {
                Locations.positions.forEach { pos ->
                    DropdownMenuItem(
                        text = { Text(pos) },
                        onClick = { onPositionChange(pos); positionExpanded = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = skillExpanded, onExpandedChange = { skillExpanded = it }) {
            OutlinedTextField(
                value = selectedSkill,
                onValueChange = {},
                readOnly = true,
                label = { Text("Skill Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = skillExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch)
            )
            ExposedDropdownMenu(expanded = skillExpanded, onDismissRequest = { skillExpanded = false }) {
                Locations.skillLevels.forEach { skill ->
                    DropdownMenuItem(
                        text = { Text(skill) },
                        onClick = { onSkillChange(skill); skillExpanded = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest)
        ) {
            Text("Continue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StepAccount(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePassword: () -> Unit,
    error: String?,
    isLoading: Boolean,
    onNext: () -> Unit
) {
    Column {
        Text(
            text = "Create your account",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink
        )
        Text(
            text = "Email and password for login",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink3,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                TextButton(onClick = onTogglePassword) {
                    Text(if (passwordVisible) "Hide" else "Show", color = Pitch2)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = email.isNotBlank() && password.length >= 6 && !isLoading,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest, disabledContainerColor = Line)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Forest, strokeWidth = 2.dp)
            } else {
                Text("Send OTP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepOtp(
    otp: String,
    onOtpChange: (String) -> Unit,
    email: String,
    error: String?,
    isLoading: Boolean,
    onVerify: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Check your email",
            style = MaterialTheme.typography.headlineMedium,
            color = Ink,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "We sent a 6-digit code to $email",
            style = MaterialTheme.typography.bodyMedium,
            color = Ink3,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = otp,
            onValueChange = { onOtpChange(it.filter { c -> c.isDigit() }.take(6)) },
            label = { Text("OTP Code") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center, letterSpacing = 8.sp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Pitch, focusedLabelColor = Pitch, cursorColor = Pitch)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerify,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = otp.length == 6 && !isLoading,
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pitch, contentColor = Forest, disabledContainerColor = Line)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Forest, strokeWidth = 2.dp)
            } else {
                Text("Verify & Create Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
