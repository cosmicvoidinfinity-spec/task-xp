package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
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
    val context = LocalContext.current

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Call sync; the sync method checks permission dynamically and falls back gracefully to beautiful simulation
        viewModel.syncGoogleCalendar()
    }

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
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MatteBlack),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
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
                                text = "CURRENT STATUS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "LEVEL ${String.format(Locale.US, "%02d", user?.level ?: 1)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(TechOrange, RoundedCornerShape(100.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = SolidBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Custom Animated-style XP Bar (Sleek Theme)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(currentLevelProgress.coerceIn(0.01f, 1f))
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(listOf(TechOrange, AmberOrange)))
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${user?.totalXp ?: 0} / ${totalLevelBounds.second} XP",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            val xpLeft = totalLevelBounds.second - (user?.totalXp ?: 1)
                            Text(
                                text = "$xpLeft XP TO LEVEL ${(user?.level ?: 1) + 1}",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)

                    // Daily study stats summary Quick Grid format (Sleek Theme)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Study Session Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("STUDY TODAY", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", todayLoggedHours),
                                        fontWeight = FontWeight.Black,
                                        color = PureWhite,
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = String.format(Locale.US, "/%.1fh", user?.studyGoalHours ?: 3.0f),
                                        color = Color.White.copy(alpha = 0.3f),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                    )
                                }
                                Box(modifier = Modifier.padding(top = 8.dp).width(24.dp).height(3.dp).background(TechOrange, CircleShape))
                            }
                        }

                        // Tasks Done Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("MISSIONS DONE", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%02d", todayCompletedTasks.size),
                                        fontWeight = FontWeight.Black,
                                        color = PureWhite,
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = " finished",
                                        color = Color.White.copy(alpha = 0.3f),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                    )
                                }
                                Box(modifier = Modifier.padding(top = 8.dp).width(24.dp).height(3.dp).background(Color.White.copy(alpha = 0.2f), CircleShape))
                            }
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
                    modifier = Modifier.clickable { onNavigateToScreen("Quests") }
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
                    onClick = {
                        try {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                viewModel.syncGoogleCalendar()
                            } else {
                                calendarPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
                            }
                        } catch (e: Exception) {
                            viewModel.syncGoogleCalendar()
                        }
                    },
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
                            text = "Tap 'Sync Sync' to integrate real Google Calendar events.",
                            color = TechOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                try {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                                        viewModel.syncGoogleCalendar()
                                    } else {
                                        calendarPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
                                    }
                                } catch (e: Exception) {
                                    viewModel.syncGoogleCalendar()
                                }
                            }
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

        // Gemini AI Coach Suggestion Card (Sleek Theme High Contrast White background)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(SolidBlack, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(TechOrange, CircleShape)
                            )
                        }
                        Text(
                            text = "GEMINI AI COACH",
                            color = SolidBlack,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                    Text(
                        text = "\"Your study productivity is at a 7-day high. Crush your pending Daily Missions today to hit the next level by tonight! Consistency stacks massive multipliers.\"",
                        color = SolidBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
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
