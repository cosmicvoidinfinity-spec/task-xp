package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudySessionEntity
import com.example.ui.XPViewModel
import com.example.ui.theme.*

@Composable
fun StudentModeScreen(viewModel: XPViewModel, onNavigateToVexa: () -> Unit) {
    val sessions by viewModel.sessionsState.collectAsState()

    val subjects = listOf("Mathematics", "Science", "Social Science", "English", "Hindi", "Computer")

    var showLogDialog by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf("Mathematics") }

    var logDuration by remember { mutableStateOf("45") }
    var logChapter by remember { mutableStateOf("") }
    var logNotes by remember { mutableStateOf("") }
    var logTestScore by remember { mutableStateOf("") }
    var isRevisionToggle by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Study header
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "ACADEMIC WARRIOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TechOrange
                )
                Text(
                    text = "SYLLABUS DIRECTORY",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Record training sessions and test metrics under key subjects to log multiplayer multipliers.",
                    fontSize = 12.sp,
                    color = MutedTextDark
                )
            }
        }

        // Subjects Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                subjects.chunked(2).forEach { rowSubjects ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowSubjects.forEach { sub ->
                            val subSessions = sessions.filter { it.subject == sub }
                            val totalMins = subSessions.sumOf { it.durationMinutes }
                            val chaptersCount = subSessions.filter { it.chapter.isNotBlank() }.distinctBy { it.chapter }.size
                            val revisionCount = subSessions.count { it.isRevision }
                            val testScores = subSessions.filter { it.testScore >= 0 }.map { it.testScore }
                            val avgScore = if (testScores.isNotEmpty()) testScores.average().toInt() else -1

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedSubject = sub
                                        showLogDialog = true
                                    },
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                                colors = CardDefaults.cardColors(containerColor = MatteBlack)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Top block
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = sub,
                                            fontWeight = FontWeight.Bold,
                                            color = TechOrange,
                                            fontSize = 14.sp
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Book,
                                            contentDescription = "sub",
                                            tint = MutedTextDark.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Stat items
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text("Focus Clock: ${totalMins}m", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Chapters Cleared: $chaptersCount", color = MutedTextDark, fontSize = 11.sp)
                                        Text("Revision Runs: $revisionCount", color = MutedTextDark, fontSize = 11.sp)
                                        if (avgScore >= 0) {
                                            Text("Avg Test Rating: $avgScore%", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            selectedSubject = sub
                                            showLogDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TechOrange.copy(alpha = 0.1f),
                                            contentColor = TechOrange
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("LOG SESSION", fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action card linking to VEXA AI for personalized diagnostic plan
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, TechOrange.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = TechOrange.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Vexa system diagnostic",
                        tint = TechOrange,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DIAGNOSE SYLLABUS STABILITY",
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Unleash VEXA's neural analyzer on your Class 9 chapters completed, revisions, and test scores to isolate weak sectors.",
                            fontSize = 11.sp,
                            color = MutedTextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onNavigateToVexa()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("RUN DIAL SYSTEM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Recent session log list
        item {
            Text(
                text = "📁 SESSION CHRONIC TRANSMISSIONS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TechOrange
            )
        }

        if (sessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MatteBlack),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Box(modifier = Modifier.padding(18.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No tracked study logs logged.", color = MutedTextDark, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(sessions) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MatteBlack),
                    border = BorderStroke(0.5.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TechOrange.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (s.isRevision) Icons.Default.Cached else Icons.Default.MenuBook,
                                contentDescription = "book icon",
                                tint = TechOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(s.subject, fontWeight = FontWeight.Bold, color = PureWhite, fontSize = 13.sp)
                                Text("${s.durationMinutes} mins", fontWeight = FontWeight.Bold, color = TechOrange, fontSize = 12.sp)
                            }
                            if (s.chapter.isNotBlank()) {
                                Text("Chapter: ${s.chapter}", color = MutedTextDark, fontSize = 11.sp)
                            }
                            if (s.notes.isNotBlank()) {
                                Text("Notes: ${s.notes}", color = MutedTextDark, fontSize = 11.sp)
                            }
                            if (s.testScore >= 0) {
                                Text("Test score achieved: ${s.testScore}%", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                        IconButton(onClick = { viewModel.deleteStudySession(s.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "delete", tint = Color.Red.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }
    }

    if (showLogDialog) {
        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            containerColor = MatteBlack,
            title = {
                Text(
                    "LOG SESSION FOR $selectedSubject",
                    fontWeight = FontWeight.Black,
                    color = TechOrange,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = logDuration,
                        onValueChange = { logDuration = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Duration (minutes)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = TechOrange,
                            unfocusedBorderColor = BorderGray,
                            focusedLabelColor = TechOrange,
                            unfocusedLabelColor = MutedTextDark
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("session_duration_input")
                    )

                    OutlinedTextField(
                        value = logChapter,
                        onValueChange = { logChapter = it },
                        label = { Text("Chapter / Topic Cleared") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = TechOrange,
                            unfocusedBorderColor = BorderGray,
                            focusedLabelColor = TechOrange,
                            unfocusedLabelColor = MutedTextDark
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = logTestScore,
                        onValueChange = { logTestScore = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Test score % (Leave empty if no test)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = TechOrange,
                            unfocusedBorderColor = BorderGray,
                            focusedLabelColor = TechOrange,
                            unfocusedLabelColor = MutedTextDark
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = logNotes,
                        onValueChange = { logNotes = it },
                        label = { Text("Study Concept Notes") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = TechOrange,
                            unfocusedBorderColor = BorderGray,
                            focusedLabelColor = TechOrange,
                            unfocusedLabelColor = MutedTextDark
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Revision Switch Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Revision Core Flag:", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = isRevisionToggle,
                            onCheckedChange = { isRevisionToggle = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = TechOrange,
                                uncheckedThumbColor = MutedTextDark,
                                uncheckedTrackColor = BorderGray
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = logDuration.toIntOrNull() ?: 45
                        val score = logTestScore.toIntOrNull() ?: -1

                        viewModel.recordStudySession(
                            subject = selectedSubject,
                            chapter = logChapter,
                            durationMinutes = mins,
                            notes = logNotes,
                            isRevision = isRevisionToggle,
                            testScore = score
                        )

                        // Clear values
                        logDuration = "45"
                        logChapter = ""
                        logNotes = ""
                        logTestScore = ""
                        isRevisionToggle = false
                        showLogDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite),
                    modifier = Modifier.testTag("submit_session_button")
                ) {
                    Text("CATALOG")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogDialog = false }) {
                    Text("ABORT", color = MutedTextDark)
                }
            }
        )
    }
}
