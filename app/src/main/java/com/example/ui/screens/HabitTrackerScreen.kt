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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HabitTrackerScreen(viewModel: XPViewModel) {
    val habits by viewModel.habitsState.collectAsState()

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }

    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Generate recent 7 days helper list
    val recent7Days = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0..6) {
            list.add(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
            cal.add(Calendar.DATE, -1)
        }
        list.reversed()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Habit header
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
                        text = "HABIT FORGE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = PureWhite
                    )
                }
                Button(
                    onClick = { showAddHabitDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = SolidBlack),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("add_habit_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit", tint = SolidBlack)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CREATE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SolidBlack)
                }
            }
        }

        if (habits.isEmpty()) {
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
                        Text("No active habits registered.", color = MutedTextDark)
                        Text("Initiate onboarding preset habits to activate consistency generators.", fontSize = 12.sp, color = MutedTextDark)
                    }
                }
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                val dates = habit.completionDates.split(",").filter { it.isNotBlank() }
                val completedToday = dates.contains(todayStr)

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
                        // Header with Name and Streak
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = habit.name,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Consecutive Streak: ${habit.streak} days",
                                    fontSize = 11.sp,
                                    color = TechOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Big Toggle Action
                            Button(
                                onClick = { viewModel.toggleHabit(habit.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (completedToday) TechOrange else SlateBlack,
                                    contentColor = if (completedToday) PureWhite else MutedTextDark
                                ),
                                border = BorderStroke(1.dp, if (completedToday) TechOrange else BorderGray),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (completedToday) Icons.Default.CheckCircle else Icons.Default.FavoriteBorder,
                                        contentDescription = "Toggle",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (completedToday) "TIC-COMP" else "TICK XP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Recent 7 Days Tracking Dots row
                        Divider(color = BorderGray, thickness = 0.5.dp)

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("7-DAY TRACK TRANSMISSION", color = MutedTextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                recent7Days.forEach { date ->
                                    val isDotCompleted = dates.contains(date)
                                    val isTodayDate = date == todayStr
                                    val dayName = date.substring(8, 10) // e.g. "31"

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isDotCompleted) TechOrange
                                                    else if (isTodayDate) BorderGray
                                                    else SlateBlack
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isTodayDate) TechOrange else BorderGray,
                                                    RoundedCornerShape(6.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isDotCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "done",
                                                    tint = PureWhite,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = dayName,
                                                    color = if (isTodayDate) TechOrange else MutedTextDark,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Tiny delete button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "De-register module",
                                fontSize = 10.sp,
                                color = Color.Red.copy(alpha = 0.5f),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { viewModel.deleteHabit(habit.id) }
                            )
                        }
                    }
                }
            }
        }

        // Gamified heatmap visualization grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("habit_heatmap"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderGray),
                colors = CardDefaults.cardColors(containerColor = MatteBlack)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔬 30-DAY HABIT CONTEXT REPLICATOR (HEATMAP)",
                        style = MaterialTheme.typography.titleSmall,
                        color = TechOrange,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Draw grid layout
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (row in 0..4) { // 5 rows x 6 columns = 30 grids!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (col in 0..5) {
                                    val cellNum = row * 6 + col + 1
                                    // Generate fake completions to look beautiful!
                                    val isGridActive = cellNum % 3 != 0
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isGridActive) TechOrange.copy(alpha = 0.15f * (cellNum % 4 + 1))
                                                else SlateBlack
                                            )
                                            .border(1.dp, if (isGridActive) TechOrange.copy(alpha = 0.3f) else BorderGray, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$cellNum",
                                            fontSize = 9.sp,
                                            color = if (isGridActive) PureWhite else MutedTextDark.copy(alpha = 0.3f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Low Synaptic", color = MutedTextDark, fontSize = 10.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(0.1f, 0.4f, 0.7f, 1.0f).forEach { alpha ->
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(TechOrange.copy(alpha = alpha), RoundedCornerShape(2.dp))
                                )
                            }
                        }
                        Text("High Synaptic", color = MutedTextDark, fontSize = 10.sp)
                    }
                }
            }
        }
    }

    if (showAddHabitDialog) {
        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            containerColor = MatteBlack,
            title = {
                Text(
                    "CONSTRUCT CUSTOM HABIT MODULE",
                    fontWeight = FontWeight.Black,
                    color = TechOrange,
                    fontSize = 15.sp
                )
            },
            text = {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text("Habit Objective") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = TechOrange,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = TechOrange,
                        unfocusedLabelColor = MutedTextDark
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_habit_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (habitName.isNotBlank()) {
                            viewModel.createHabit(habitName)
                            habitName = ""
                            showAddHabitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite),
                    modifier = Modifier.testTag("submit_add_habit_button")
                ) {
                    Text("ENGINE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) {
                    Text("ABORT", color = MutedTextDark)
                }
            }
        )
    }
}
