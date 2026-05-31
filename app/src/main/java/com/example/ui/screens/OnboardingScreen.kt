package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.XPViewModel
import com.example.ui.theme.*
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(viewModel: XPViewModel) {
    var step by remember { mutableStateOf(0) } // Start on authentication screen step 0
    var name by remember { mutableStateOf("") }
    var schoolClass by remember { mutableStateOf("Class 9") }
    var studyGoalHours by remember { mutableStateOf("3.0") }

    // Authentication values
    var userEmail by remember { mutableStateOf("") }
    var userProvider by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var showEmailForm by remember { mutableStateOf(false) }

    // Dialog state controllers
    var showGoogleDialog by remember { mutableStateOf(false) }
    var showFacebookDialog by remember { mutableStateOf(false) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var authProgressMessage by remember { mutableStateOf("") }

    val userFlowState by viewModel.userState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Selecting starter habits
    val presetHabits = listOf(
        "Wake Up Early",
        "Perform Math Revision",
        "Deep Science Reading",
        "30m Evening Walk",
        "Zen Breathing Mode",
        "Drink 3 Liters Water"
    )
    val selectedHabits = remember { mutableStateListOf(*presetHabits.toTypedArray()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SlateBlack, SolidBlack)
                )
            )
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .border(2.dp, TechOrange, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(MatteBlack)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Title Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = "RPG icon",
                    tint = TechOrange,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "XP PLANNER SYSTEM",
                    style = MaterialTheme.typography.titleLarge,
                    color = TechOrange,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            LinearProgressIndicator(
                progress = { step / 3.0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = TechOrange,
                trackColor = BorderGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { width -> width / 2 } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width / 2 } + fadeOut()
                },
                label = "step_transition"
            ) { targetStep ->
                when (targetStep) {
                    0 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "AUTH PORTAL SYNC",
                                style = MaterialTheme.typography.headlineSmall,
                                color = PureWhite,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Choose an authorization portal to synchronize class records and quest logs.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedTextDark,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // 1. Google/Gmail Button
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clickable { showGoogleDialog = true }
                                    .testTag("google_auth_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                colors = CardDefaults.cardColors(containerColor = SlateBlack)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(PureWhite, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "G",
                                            color = Color(0xFF4285F4),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Continue with Google / Gmail",
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 2. Facebook Button
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clickable { showFacebookDialog = true }
                                    .testTag("facebook_auth_btn"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                colors = CardDefaults.cardColors(containerColor = SlateBlack)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFF1877F2), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "f",
                                            color = PureWhite,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Continue with Facebook",
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 3. Email and Password Accordion Form
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (showEmailForm) TechOrange else BorderGray),
                                colors = CardDefaults.cardColors(containerColor = SlateBlack)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showEmailForm = !showEmailForm },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = "Email Icon",
                                                tint = if (showEmailForm) TechOrange else MutedTextDark,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = "Sign in with Email or Gmail Account",
                                                color = PureWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Text(
                                            text = if (showEmailForm) "COLLAPSE" else "EXPAND",
                                            color = TechOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (showEmailForm) {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedTextField(
                                            value = emailInput,
                                            onValueChange = { emailInput = it },
                                            label = { Text("Email/Gmail Address") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = TechOrange,
                                                unfocusedBorderColor = BorderGray,
                                                focusedLabelColor = TechOrange,
                                                focusedTextColor = PureWhite,
                                                unfocusedTextColor = PureWhite
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("email_input_field"),
                                            shape = RoundedCornerShape(8.dp),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                            value = passwordInput,
                                            onValueChange = { passwordInput = it },
                                            label = { Text("Cyber Passphrase") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = TechOrange,
                                                unfocusedBorderColor = BorderGray,
                                                focusedLabelColor = TechOrange,
                                                focusedTextColor = PureWhite,
                                                unfocusedTextColor = PureWhite
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("password_input_field"),
                                            shape = RoundedCornerShape(8.dp),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                if (emailInput.isNotBlank() && emailInput.contains("@")) {
                                                    isAuthenticating = true
                                                    authProgressMessage = "Decrypting cybercredential keys for safe email sync..."
                                                    coroutineScope.launch {
                                                        delay(1500)
                                                        isAuthenticating = false
                                                        
                                                        val finalEmail = emailInput.trim()
                                                        if (userFlowState != null) {
                                                            viewModel.signInUser(finalEmail, "Email")
                                                        } else {
                                                            userEmail = finalEmail
                                                            userProvider = "Email"
                                                            // Prefill name dynamically
                                                            name = finalEmail.substringBefore("@")
                                                                .split(".", "_", "-")
                                                                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() } }
                                                            step = 1
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().height(44.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = TechOrange,
                                                contentColor = PureWhite
                                            ),
                                            enabled = emailInput.isNotBlank() && emailInput.contains("@"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("AUTHORIZE CONNECTION", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "CONTINUE IN OFFLINE SIMULATION MODE",
                                color = TechOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clickable {
                                        userEmail = "offline.expert@vexa.net"
                                        userProvider = "Guest"
                                        name = "Expert Cadet"
                                        step = 1
                                    }
                                    .padding(8.dp)
                                    .testTag("guest_bypass_btn")
                            )
                        }
                    }
                    1 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "INITIALIZING PLOT",
                                style = MaterialTheme.typography.headlineSmall,
                                color = PureWhite,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create your Class 9 study avatar to unlock levels and game-style XP.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedTextDark,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Avatar Name / Callsign") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TechOrange,
                                    unfocusedBorderColor = BorderGray,
                                    focusedLabelColor = TechOrange,
                                    unfocusedLabelColor = MutedTextDark,
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("avatar_name_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = schoolClass,
                                onValueChange = { schoolClass = it },
                                label = { Text("Level / Grade Class (e.g. Class 9)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TechOrange,
                                    unfocusedBorderColor = BorderGray,
                                    focusedLabelColor = TechOrange,
                                    unfocusedLabelColor = MutedTextDark,
                                    focusedTextColor = PureWhite,
                                    unfocusedTextColor = PureWhite
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }
                    2 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "DAILY FOCUS LEVEL",
                                style = MaterialTheme.typography.headlineSmall,
                                color = PureWhite,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "How many hours can you commit to deep study daily? We recommend 3.0 to earn maximum study multipliers.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedTextDark,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                                    .background(SlateBlack)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Hours Goal",
                                    tint = TechOrange,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Daily Focus Goal", color = PureWhite, fontWeight = FontWeight.Bold)
                                    Text("Class 9 standard is 3-4 hours", color = MutedTextDark, fontSize = 12.sp)
                                }
                                OutlinedTextField(
                                    value = studyGoalHours,
                                    onValueChange = { studyGoalHours = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = TechOrange,
                                        unfocusedBorderColor = BorderGray,
                                        focusedTextColor = PureWhite,
                                        unfocusedTextColor = PureWhite
                                    ),
                                    modifier = Modifier.width(80.dp),
                                    singleLine = true
                                )
                            }
                        }
                    }
                    3 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "CHOOSE HABITS",
                                style = MaterialTheme.typography.headlineSmall,
                                color = PureWhite,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Select active habit modules. Earn +15 XP each check-in, plus massive perfect day streak multipliers.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedTextDark,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectableGroup(),
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                presetHabits.forEach { habit ->
                                    val isSelected = selectedHabits.contains(habit)
                                    Card(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clickable {
                                                if (isSelected) selectedHabits.remove(habit)
                                                else selectedHabits.add(habit)
                                            },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) TechOrange else SlateBlack,
                                            contentColor = if (isSelected) PureWhite else MutedTextDark
                                        ),
                                        border = if (isSelected) null else BorderStroke(1.dp, BorderGray)
                                    ) {
                                        Text(
                                            text = habit,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (step > 0) {
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            // Complete onboarding and setup database state!
                            val hours = studyGoalHours.toFloatOrNull() ?: 3.0f
                            viewModel.completeOnboarding(
                                name = name,
                                studentClass = schoolClass,
                                goalHours = hours,
                                initialHabits = selectedHabits.toList(),
                                email = userEmail,
                                loginProvider = userProvider
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechOrange,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (step == 3) "LAUNCH TACTICAL SYSTEM" else "CONTINUE STAGE",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next"
                        )
                    }
                }
            }
        }
    }

    // SIMULATED GOOGLE OAUTH SELECTOR DIALOG
    if (showGoogleDialog) {
        Dialog(onDismissRequest = { showGoogleDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MatteBlack),
                border = BorderStroke(1.dp, BorderGray),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(PureWhite, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sign in with Google", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("to continue to VEXA STUDENT PLANNER", color = MutedTextDark, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val accounts = listOf(
                        "cosmicvoidinfinity@gmail.com" to "Cosmic Void (Active)",
                        "student.prep9@gmail.com" to "Level 1 Cadet Profile"
                    )

                    accounts.forEach { (emailStr, subtitleStr) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateBlack),
                            border = BorderStroke(0.5.dp, BorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    showGoogleDialog = false
                                    isAuthenticating = true
                                    authProgressMessage = "Linking token with Google accounts API secure server..."
                                    coroutineScope.launch {
                                        delay(1500)
                                        isAuthenticating = false
                                        if (userFlowState != null) {
                                            viewModel.signInUser(emailStr, "Google")
                                        } else {
                                            userEmail = emailStr
                                            userProvider = "Google"
                                            name = emailStr.substringBefore("@")
                                                .split(".", "_", "-")
                                                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() } }
                                            step = 1
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "avatar",
                                    tint = TechOrange,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(emailStr, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(subtitleStr, color = MutedTextDark, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    var showCustomGoogleMailInput by remember { mutableStateOf(false) }
                    var customGoogleMail by remember { mutableStateOf("") }

                    if (showCustomGoogleMailInput) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customGoogleMail,
                            onValueChange = { customGoogleMail = it },
                            label = { Text("Gmail or Google address") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TechOrange,
                                unfocusedBorderColor = BorderGray,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (customGoogleMail.contains("@")) {
                                    showGoogleDialog = false
                                    isAuthenticating = true
                                    authProgressMessage = "Syncing Custom Gmail credentials with secure Google OAuth nodes..."
                                    coroutineScope.launch {
                                        delay(1500)
                                        isAuthenticating = false
                                        val finalEmail = customGoogleMail.trim()
                                        if (userFlowState != null) {
                                            viewModel.signInUser(finalEmail, "Google")
                                        } else {
                                            userEmail = finalEmail
                                            userProvider = "Google"
                                            name = finalEmail.substringBefore("@")
                                                .split(".", "_", "-")
                                                .joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() } }
                                            step = 1
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechOrange),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = customGoogleMail.contains("@")
                        ) {
                            Text("USE ACCOUNT", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+ Use another Gmail account",
                            color = TechOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCustomGoogleMailInput = true }
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { showGoogleDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = MutedTextDark),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("CANCEL")
                    }
                }
            }
        }
    }

    // SIMULATED FACEBOOK PORTAL DIALOG
    if (showFacebookDialog) {
        Dialog(onDismissRequest = { showFacebookDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1877F2)),
                border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(PureWhite, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("f", color = Color(0xFF1877F2), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Facebook Link Security", color = PureWhite, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Vexa Study Planner is asking to access your profile picture, public avatar, and registered contact email (cosmicvoidinfinity@gmail.com).",
                        color = PureWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showFacebookDialog = false
                            isAuthenticating = true
                            authProgressMessage = "Connecting with safe Facebook Graph nodes..."
                            coroutineScope.launch {
                                delay(1500)
                                isAuthenticating = false
                                val fbEmail = "cosmicvoidinfinity@gmail.com"
                                if (userFlowState != null) {
                                    viewModel.signInUser(fbEmail, "Facebook")
                                } else {
                                    userEmail = fbEmail
                                    userProvider = "Facebook"
                                    name = "Cosmic Void"
                                    step = 1
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PureWhite, contentColor = Color(0xFF1877F2)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("CONTINUE AS COSMIC VOID", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showFacebookDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = PureWhite.copy(alpha = 0.8f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CANCEL CONNECTION", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // AUTH PROCESS TELEMETRY OVERLAY
    if (isAuthenticating) {
        Dialog(onDismissRequest = {}) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MatteBlack),
                border = BorderStroke(2.dp, TechOrange),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = TechOrange, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = authProgressMessage,
                        color = PureWhite,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
