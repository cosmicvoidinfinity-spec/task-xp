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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
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
import com.example.ui.XPViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(viewModel: XPViewModel) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var schoolClass by remember { mutableStateOf("Class 9") }
    var studyGoalHours by remember { mutableStateOf("3.0") }

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
            .padding(24.dp)
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
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Title Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = "RPG icon",
                    tint = TechOrange,
                    modifier = Modifier.size(32.dp)
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

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    slideInHorizontally { width -> width / 2 } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width / 2 } + fadeOut()
                },
                label = "step_transition"
            ) { targetStep ->
                when (targetStep) {
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
                                label = { Text("Avatar Name/Calls") },
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
                            initialHabits = selectedHabits.toList()
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
