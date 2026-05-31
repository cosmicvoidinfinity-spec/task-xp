package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.XPViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

fun getXpProgressForLevel(xp: Int, level: Int): Float {
    val bounds = getXpRangeForLevel(level)
    val minXp = bounds.first
    val maxXp = bounds.second
    val levelEarned = xp - minXp
    val levelTotal = maxXp - minXp
    if (levelTotal <= 0) return 0f
    return (levelEarned.toFloat() / levelTotal).coerceIn(0f, 1f)
}

fun getXpRangeForLevel(level: Int): Pair<Int, Int> {
    return when (level) {
        1 -> Pair(0, 250)
        2 -> Pair(250, 600)
        3 -> Pair(600, 1200)
        4 -> Pair(1200, 2000)
        5 -> Pair(2000, 3200)
        6 -> Pair(3200, 5000)
        7 -> Pair(5000, 7500)
        8 -> Pair(7500, 10000)
        9 -> Pair(10000, 15000)
        else -> Pair(15000, 30000)
    }
}

@Composable
fun DashboardScreen(viewModel: XPViewModel, onNavigateToScreen: (String) -> Unit) {
    val user by viewModel.userState.collectAsState()
    val tasks by viewModel.tasksState.collectAsState()
    val habits by viewModel.habitsState.collectAsState()
    val sessions by viewModel.sessionsState.collectAsState()
    val calendarEvents by viewModel.calendarEventsState.collectAsState()

    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Aggregate statistics
    val todayCompletedTasks = tasks.filter { it.isCompleted && isToday(it.completedAt) }
    val todayPendingTasks = tasks.filter { !it.isCompleted }
    val todayLoggedHours = sessions.filter { isToday(it.timestamp) }.sumOf { it.durationMinutes } / 60.0f

    val totalLevelBounds = user?.level?.let { getXpRangeForLevel(it) } ?: Pair(0, 250)
    val currentLevelProgress = user?.let { getXpProgressForLevel(it.totalXp, it.level) } ?: 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Profile Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MatteBlack),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                Brush.linearGradient(listOf(TechOrange, AmberOrange)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gamepad,
                            contentDescription = "Avatar Profile Icon",
                            tint = PureWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "COMMANDER: ${user?.name ?: "Student"}",
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Class Icon",
                                tint = TechOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.studentClass ?: "Class 9"} Grid",
                                color = MutedTextDark,
                                fontSize = 13.sp
                            )
                        }
                    }
                    // Fire streak indicator
                    Row(
                        modifier = Modifier
                            .border(1.dp, TechOrange, RoundedCornerShape(12.dp))
                            .background(TechOrange.copy(alpha = 0.15f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Streak Fire",
                            tint = TechOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.currentStreak ?: 1}D",
                            color = PureWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Gamified Status (Level, Goal Progress)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Level text & total XP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "TACTICAL LEVEL: ${user?.level ?: 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = TechOrange
                            )
                            Text(
                                text = "Next Level in: ${totalLevelBounds.second - (user?.totalXp ?: 0)} XP",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedTextDark
                            )
                        }
                        Text(
                            text = "${user?.totalXp ?: 0} XP",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = PureWhite
                        )
                    }

                    // Level Up Progress Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { currentLevelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .testTag("score_progress_bar"),
                            color = TechOrange,
                            trackColor = BorderGray
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${totalLevelBounds.first} XP", color = MutedTextDark, fontSize = 11.sp)
                            Text("${totalLevelBounds.second} XP", color = MutedTextDark, fontSize = 11.sp)
                        }
                    }

                    Divider(color = BorderGray, thickness = 1.dp)

                    // Daily study stats summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("STUDY SESSION", fontSize = 10.sp, color = MutedTextDark, fontWeight = FontWeight.Bold)
                            Text(
                                text = String.format(Locale.US, "%.1f / %.1f h", todayLoggedHours, user?.studyGoalHours ?: 3.0f),
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhite,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(BorderGray))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("TASKS DONE", fontSize = 10.sp, color = MutedTextDark, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${todayCompletedTasks.size} done",
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhite,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(BorderGray))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("HABIT TICKS", fontSize = 10.sp, color = MutedTextDark, fontWeight = FontWeight.Bold)
                            val tickedToday = habits.count { it.completionDates.split(",").contains(todayDate) }
                            Text(
                                text = "$tickedToday / ${habits.size}",
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhite,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Today's Missions List Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚨 DAILY MISSIONS (${todayPendingTasks.size} PENDING)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TechOrange
                )
                Text(
                    text = "View All",
                    color = MutedTextDark,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onNavigateToScreen("Tasks") }
                )
            }
        }

        if (todayPendingTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateBlack),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "All clean",
                            tint = TechOrange,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Daily Quests Cleared!",
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Text(
                            text = "Create more tasks or launch a study Pomodoro block to earn bonus multipliers.",
                            fontSize = 12.sp,
                            color = MutedTextDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(todayPendingTasks.take(3)) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MatteBlack),
                    border = BorderStroke(0.5.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTask(task.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TechOrange,
                                checkmarkColor = PureWhite
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = TechOrange.copy(alpha = 0.1f)),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = task.category,
                                        color = TechOrange,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Reward: +${task.xpReward} XP",
                                    fontSize = 11.sp,
                                    color = MutedTextDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // Habit Daily Quick Toggle Dashboard Row
        item {
            Text(
                text = "⚡ HABIT POWER-UPS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TechOrange
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                habits.forEach { habit ->
                    val completedToday = habit.completionDates.split(",").contains(todayDate)
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (completedToday) TechOrange else BorderGray,
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (completedToday) TechOrange.copy(alpha = 0.15f) else MatteBlack)
                            .clickable { viewModel.toggleHabit(habit.id) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (completedToday) Icons.Default.CheckCircle else Icons.Default.FavoriteBorder,
                                contentDescription = "heart tick",
                                tint = if (completedToday) TechOrange else MutedTextDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = habit.name,
                                color = if (completedToday) PureWhite else MutedTextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Today's Google Calendar Agenda Sync Block
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 GOOGLE CALENDAR AGENDA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TechOrange
                )
                Button(
                    onClick = { viewModel.syncGoogleCalendar() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechOrange.copy(alpha = 0.2f),
                        contentColor = TechOrange
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (calendarEvents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MatteBlack),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No today scheduled sync events.",
                            color = MutedTextDark,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tap 'Sync Sync' to simulate import from Google Calendar.",
                            color = TechOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.syncGoogleCalendar() }
                        )
                    }
                }
            }
        } else {
            items(calendarEvents) { event ->
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val startStr = timeFormat.format(Date(event.startTime))
                val endStr = timeFormat.format(Date(event.endTime))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateBlack),
                    border = BorderStroke(0.5.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Event icon",
                            tint = TechOrange,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "$startStr - $endStr",
                                color = MutedTextDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Daily Motivation Quote Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TechOrange),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "\"Consistency always outperforms pure talent. The small XP multipliers you stack today build Level 10 Legend Status tomorrow.\"",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "- VEXA SYSTEM MATRIX COUNSEL",
                        color = PureWhite.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun isToday(timestamp: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal2.timeInMillis = timestamp
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
