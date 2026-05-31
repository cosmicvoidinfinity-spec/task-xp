package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.XPViewModel
import com.example.ui.theme.*

data class BadgeDefinition(
    val name: String,
    val description: String,
    val effect: String
)

@Composable
fun ScoreboardScreen(viewModel: XPViewModel) {
    val user by viewModel.userState.collectAsState()
    val tasks by viewModel.tasksState.collectAsState()
    val habits by viewModel.habitsState.collectAsState()
    val sessions by viewModel.sessionsState.collectAsState()
    val challenges by viewModel.challengesState.collectAsState()

    // Unlocked badges list parsed from comma separated string
    val unlockedBadgesSet = remember(user?.unlockedBadges) {
        user?.unlockedBadges?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }

    val badgesList = listOf(
        BadgeDefinition("Beginner", "Flogged your starter Class 9 profile dossier.", "+10% Starter Multiplier"),
        BadgeDefinition("Consistent", "Maintained an active login sequence of 3+ days.", "Daily XP Fire active"),
        BadgeDefinition("Focused Student", "Recorded your first syllabus study revision session.", "+20 XP study multi"),
        BadgeDefinition("Study Warrior", "Logged 5+ revision session blocks or 3+ study hours.", "Academic multiplier"),
        BadgeDefinition("Habit Master", "Checked habit check-in logs over 10 cumulative times.", "Streaks retention +1D"),
        BadgeDefinition("Discipline King", "Forged a consecutive active streak of 7+ days.", "+5% Focus retention"),
        BadgeDefinition("Productivity Champion", "Defeated and solved 15+ quest objectives.", "Overload tasks unlock"),
        BadgeDefinition("30-Day Hero", "Logged at least 15 days or fully cleared a 30-Day Mission.", "Mega challenge buffer"),
        BadgeDefinition("XP Legend", "Scaled to Level 5+ or gathered over 2500 total XP.", "+50% XP multiplier")
    )

    // Compute metrics
    val totalDoneTasks = tasks.count { it.isCompleted }
    val totalSessionsCount = sessions.size
    val totalHoursLogged = sessions.sumOf { it.durationMinutes } / 60f
    val habitsTicked = habits.sumOf { h -> h.completionDates.split(",").filter { it.isNotBlank() }.size }
    val challengesCount = challenges.size
    val challengesCleared = challenges.count { it.isCompleted }

    // Average productivity rating calculation: 0 - 100%
    val rawProductivityRating = if (tasks.isNotEmpty() || habits.isNotEmpty()) {
        val completedRatio = if (tasks.isNotEmpty()) totalDoneTasks.toFloat() / tasks.size else 1f
        val habitsRatio = if (habits.isNotEmpty()) habitsTicked.toFloat() / (habits.size * 3) else 1f // estimate factor
        (((completedRatio + habitsRatio) / 2f) * 100).coerceIn(10f, 100f).toInt()
    } else {
        75
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats grid block
        Text(
            text = "🏅 DOSSIER PERFORMANCE INDEX",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = TechOrange
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderGray),
            colors = CardDefaults.cardColors(containerColor = MatteBlack)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("TOTAL XP EARNED", color = MutedTextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${user?.totalXp ?: 0} XP", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TACTICAL CLASS", color = MutedTextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(user?.studentClass ?: "Class 9", color = TechOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }

                Divider(color = BorderGray, thickness = 0.5.dp)

                // Sub metrics table
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Longest Active Streak:", color = MutedTextDark, fontSize = 12.sp)
                        Text("${user?.longestStreak ?: 1} Days", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Completed Quests:", color = MutedTextDark, fontSize = 12.sp)
                        Text("$totalDoneTasks objects", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Habit check-in counters:", color = MutedTextDark, fontSize = 12.sp)
                        Text("$habitsTicked times", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Study Syllabus hours:", color = MutedTextDark, fontSize = 12.sp)
                        Text(String.format("%.1f Hours", totalHoursLogged), color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mission Completion Index:", color = MutedTextDark, fontSize = 12.sp)
                        Text("$challengesCleared / $challengesCount challenges", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TACTICAL MATRIX EFFICIENCY:", color = TechOrange, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        Text("$rawProductivityRating%", color = TechOrange, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }

        // BADGE COLLECTION GALLERY LIST
        Text(
            text = "🏆 NEURAL BADGES UNLOCKED (${unlockedBadgesSet.size} / ${badgesList.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TechOrange
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f).testTag("scoreboard_grid")
        ) {
            items(badgesList) { badge ->
                val isUnlocked = unlockedBadgesSet.contains(badge.name)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = if (isUnlocked) 1.dp else 0.5.dp,
                        color = if (isUnlocked) TechOrange else BorderGray
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) MatteBlack else SlateBlack.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Badge Image Icon placeholder
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isUnlocked) Brush.linearGradient(listOf(TechOrange, AmberOrange))
                                    else Brush.linearGradient(listOf(BorderGray, SlateBlack))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.WorkspacePremium else Icons.Default.Lock,
                                contentDescription = badge.name,
                                tint = if (isUnlocked) PureWhite else MutedTextDark.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = badge.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = if (isUnlocked) PureWhite else MutedTextDark,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = badge.description,
                            fontSize = 10.sp,
                            color = MutedTextDark,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp,
                            modifier = Modifier.height(26.dp)
                        )

                        // Buff text
                        Text(
                            text = if (isUnlocked) badge.effect else "LOCKED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) TechOrange else MutedTextDark.copy(alpha = 0.3f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
