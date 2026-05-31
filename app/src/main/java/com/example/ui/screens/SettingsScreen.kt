package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.XPViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: XPViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onResetOnboarding: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var mockBackupSynched by remember { mutableStateOf(false) }

    var isSyncingState by remember { mutableStateOf(false) }

    LaunchedEffect(isSyncingState) {
        if (isSyncingState) {
            kotlinx.coroutines.delay(2000)
            isSyncingState = false
            mockBackupSynched = true
            viewModel.addManualXp(50, "Completed Synced Backup Database to Cloud")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "⚙️ SYSTEMS CONTROL BOARD",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = TechOrange
        )

        Divider(color = BorderGray, thickness = 0.5.dp)

        // 1. Core Visual Matrix Control
        Card(
            modifier = Modifier.fillMaxWidth().testTag("theme_settings_card"),
            border = BorderStroke(0.5.dp, BorderGray),
            colors = CardDefaults.cardColors(containerColor = MatteBlack)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "VISUAL DIMENSION (THEMING)",
                    color = TechOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Dark Tactical Layout", color = PureWhite, fontWeight = FontWeight.Bold)
                        Text("Orange/Black futuristic RPG appearance", color = MutedTextDark, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = TechOrange,
                            uncheckedThumbColor = MutedTextDark,
                            uncheckedTrackColor = BorderGray
                        )
                    )
                }
            }
        }

        // 2. Transceiver notifications Reminders
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(0.5.dp, BorderGray),
            colors = CardDefaults.cardColors(containerColor = MatteBlack)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "RESONATOR ALERTS (NOTIFICATIONS)",
                    color = TechOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Morning Syllabus Prompts", color = PureWhite, fontWeight = FontWeight.Bold)
                        Text("Tactical prep sequence updates at 08:00", color = MutedTextDark, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureWhite,
                            checkedTrackColor = TechOrange,
                            uncheckedThumbColor = MutedTextDark,
                            uncheckedTrackColor = BorderGray
                        )
                    )
                }
            }
        }

        // 3. Cloud synching simulation
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(0.5.dp, BorderGray),
            colors = CardDefaults.cardColors(containerColor = MatteBlack)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "DATABASE FIREBASE CLOUD HYBRID SYNC",
                    color = TechOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Text(
                    text = "Sync SQLite offline structures with Firebase Firestore nodes. Offline support completes calculations, and schedules local backups automatically.",
                    color = MutedTextDark,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                if (isSyncingState) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = TechOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("SYNCHRONIZING SECURE TACTICAL PACKETS...", color = TechOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = { isSyncingState = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "cloud sync", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SAVE SYNCHRONIZED ARCHIVE (+50 XP)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                if (mockBackupSynched) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TechOrange.copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = "done", tint = TechOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Firestore Cloud Node completely integrated. Status Green.", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Reset & Destabilization Node
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.03f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "AVATAR DESTABILIZATION CORE",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    "Purges user table matrices with zero backup retention. Resets onboarding configurations.",
                    color = MutedTextDark,
                    fontSize = 11.sp
                )
                Button(
                    onClick = { onResetOnboarding() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = PureWhite),
                    modifier = Modifier.fillMaxWidth().testTag("reset_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("DESTRUCT CURRENT AVATAR PROFILE", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }
}
