package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.XPViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MissionPlannerScreen(viewModel: XPViewModel) {
    val challenges by viewModel.challengesState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var challengeTitle by remember { mutableStateOf("") }
    var challengeType by remember { mutableStateOf("Study Challenge") }

    val challengeTypes = listOf(
        "Study Challenge",
        "Exercise Challenge",
        "Reading Challenge",
        "No Procrastination Challenge",
        "Custom Challenge"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACADEMIC WARRIOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = TechOrange
                    )
                    Text(
                        text = "MEGA MISSION",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = PureWhite
                    )
                }
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = SolidBlack),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("add_challenge_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Challenge", tint = SolidBlack)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("START", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SolidBlack)
                }
            }
        }

        if (challenges.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MatteBlack),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No active 30-Day Missions launched.", color = MutedTextDark)
                        Text("Forge a challenge grid to embark on Level 10 Legend preparation.", fontSize = 12.sp, color = MutedTextDark, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(challenges, key = { it.id }) { challenge ->
                val completedList = challenge.completedDays.split(",").filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() }.toSet()
                val progressPercent = (completedList.size / 30f) * 100f

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    colors = CardDefaults.cardColors(containerColor = MatteBlack)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = challenge.title,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PureWhite,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Challenge: ${challenge.challengeType}",
                                    fontSize = 12.sp,
                                    color = TechOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { viewModel.deleteChallenge(challenge.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "delete", tint = Color.Red.copy(alpha = 0.5f))
                            }
                        }

                        // Progress Indicator
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("COMPLETION SYNAPSE", color = MutedTextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("${completedList.size} / 30 Days (${progressPercent.toInt()}%)", color = PureWhite, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                            LinearProgressIndicator(
                                progress = { completedList.size / 30f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = TechOrange,
                                trackColor = BorderGray
                            )
                        }

                        Divider(color = BorderGray, thickness = 0.5.dp)

                        // 30 BOX CALENDAR GRID
                        Text("TAP CURRENT DAY TO LOG FOCUS LEVEL (+20 XP):", color = MutedTextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (row in 0..4) { // 5 rows
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (col in 1..6) { // 6 columns = 30 boxes grid!
                                        val dayNum = row * 6 + col
                                        val isTicked = completedList.contains(dayNum)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isTicked) TechOrange else SlateBlack)
                                                .border(1.dp, if (isTicked) TechOrange else BorderGray, RoundedCornerShape(8.dp))
                                                .clickable { viewModel.tickChallengeDay(challenge.id, dayNum) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isTicked) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "completed day",
                                                    tint = PureWhite,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = "$dayNum",
                                                    color = MutedTextDark,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Unlock Reward status
                        if (challenge.isCompleted) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = TechOrange.copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = TechOrange)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("30-DAY HERO BADGE UNLOCKED", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("+500 Bonus XP successfully fused to avatar.", color = TechOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = MatteBlack,
            title = {
                Text(
                    "LAUNCH NEW 30-DAY MISSION CHALLENGE",
                    fontWeight = FontWeight.Black,
                    color = TechOrange,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = challengeTitle,
                        onValueChange = { challengeTitle = it },
                        label = { Text("Mission Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = TechOrange,
                            unfocusedBorderColor = BorderGray,
                            focusedLabelColor = TechOrange,
                            unfocusedLabelColor = MutedTextDark
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_challenge_title_input")
                    )

                    Text("Challenge Type Selector:", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        challengeTypes.forEach { type ->
                            val isSelected = challengeType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TechOrange else SlateBlack)
                                    .clickable { challengeType = type }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(type, color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (challengeTitle.isNotBlank()) {
                            viewModel.createChallenge(challengeTitle, challengeType)
                            challengeTitle = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite),
                    modifier = Modifier.testTag("submit_add_challenge_button")
                ) {
                    Text("DEPLOY GRID")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("ABORT", color = MutedTextDark)
                }
            }
        )
    }
}
