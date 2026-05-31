package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class XPRepository(context: Context) {
    private val db = XPDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val taskDao = db.taskDao()
    private val habitDao = db.habitDao()
    private val challengeDao = db.challengeDao()
    private val studySessionDao = db.studySessionDao()
    private val xpLogDao = db.xpLogDao()
    private val calendarEventDao = db.calendarEventDao()

    // Expose Data Flows
    val userFlow: Flow<UserEntity?> = userDao.getUserFlow()
    val allTasksFlow: Flow<List<TaskEntity>> = taskDao.getAllTasksFlow()
    val allHabitsFlow: Flow<List<HabitEntity>> = habitDao.getAllHabitsFlow()
    val allChallengesFlow: Flow<List<ChallengeEntity>> = challengeDao.getAllChallengesFlow()
    val allSessionsFlow: Flow<List<StudySessionEntity>> = studySessionDao.getAllSessionsFlow()
    val allXpLogsFlow: Flow<List<XpLogEntity>> = xpLogDao.getAllXpLogsFlow()
    val allCalendarEventsFlow: Flow<List<CalendarEventEntity>> = calendarEventDao.getAllCalendarEventsFlow()

    // Date formatting helper
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private fun getTodayDateString(): String = dateFormat.format(Date())

    // Onboarding starter values
    suspend fun setupStarterOnboarding(name: String, studentClass: String, goalHours: Float, habitList: List<String>) {
        val today = getTodayDateString()
        val defaultUser = UserEntity(
            id = 1,
            name = name.ifBlank { "Class 9 Student" },
            studentClass = studentClass.ifBlank { "Class 9" },
            studyGoalHours = if (goalHours > 0) goalHours else 3.0f,
            habitGoals = habitList.joinToString(","),
            totalXp = 50, // Starter bonus!
            level = 1,
            currentStreak = 1,
            longestStreak = 1,
            lastActiveDate = today,
            unlockedBadges = "Beginner"
        )
        userDao.insertOrUpdateUser(defaultUser)
        logXpGain(50, "Completed Planner SetupOnboarding")

        // Populate standard default habits
        val standardHabits = listOf(
            "Wake Up Early",
            "Study Space",
            "Daily Reading",
            "Exercise (XP Fit)",
            "Meditation (Zen Grid)",
            "Drink 3L Water"
        )
        for (hName in standardHabits) {
            habitDao.insertHabit(HabitEntity(name = hName, isCustom = false))
        }

        // Add default tasks
        taskDao.insertTask(TaskEntity(title = "Review Math Quadratic Equations Ch 1", category = "Study", priority = "High", xpReward = 50))
        taskDao.insertTask(TaskEntity(title = "Finish Homework notes for Science Ch 3", category = "Homework", priority = "Medium", xpReward = 25))
        taskDao.insertTask(TaskEntity(title = "Read 5 pages of English story book", category = "Reading", priority = "Low", xpReward = 10))

        // Prepopulate a 30-Day study challenge
        challengeDao.insertChallenge(ChallengeEntity(
            title = "Class 9 Boards Prepper Study Challenge",
            challengeType = "Study Challenge"
        ))

        // Prepopulate Calendar Events for Demo
        val calendar = Calendar.getInstance()
        calendarEventDao.insertCalendarEvent(
            CalendarEventEntity(
                title = "Study Session: Mathematics Ch 4 Revision",
                startTime = calendar.timeInMillis + 7200000, // 2 hours from now
                endTime = calendar.timeInMillis + 10800000   // 3 hours from now
            )
        )
    }

    // Check levels & active status
    suspend fun checkDailyStreak() {
        val user = userDao.getUserSync() ?: return
        val today = getTodayDateString()
        if (user.lastActiveDate == today) return // already active today

        val yesterday = getYesterdayDateString()
        val newStreak = if (user.lastActiveDate == yesterday) {
            user.currentStreak + 1
        } else {
            1 // broken streak
        }

        val longest = if (newStreak > user.longestStreak) newStreak else user.longestStreak

        // Award Streak Bonuses
        var xpBonus = 0
        var streakMsg = "Daily login streak active"
        if (newStreak == 7) {
            xpBonus = 250
            streakMsg = "Unlocked 7-Day Streak Bonus!"
        } else if (newStreak == 30) {
            xpBonus = 1000
            streakMsg = "Unlocked 30-Day Mega Streak Bonus!"
        }

        val updatedUser = user.copy(
            currentStreak = newStreak,
            longestStreak = longest,
            lastActiveDate = today,
            totalXp = user.totalXp + xpBonus
        )
        userDao.insertOrUpdateUser(updatedUser)

        if (xpBonus > 0) {
            logXpGain(xpBonus, streakMsg)
        }
        recalculateLevelAndBadges()
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return dateFormat.format(cal.time)
    }

    // ==========================================
    // XP & GAMIFICATION ENGINE
    // ==========================================

    suspend fun logXpGain(amount: Int, reason: String) {
        xpLogDao.insertXpLog(XpLogEntity(amount = amount, reason = reason))
        val user = userDao.getUserSync() ?: return
        val newXp = user.totalXp + amount
        userDao.insertOrUpdateUser(user.copy(totalXp = newXp))
        recalculateLevelAndBadges()
    }

    suspend fun recalculateLevelAndBadges() {
        val user = userDao.getUserSync() ?: return
        val xp = user.totalXp

        // Level Bounds Check
        // Level 1 = 0 XP, L2 = 250, L3 = 600, L4 = 1200, L5 = 2000, L6 = 3200, L7 = 5000, L8 = 7500, L9 = 10000, L10 = 15000
        val computedLevel = when {
            xp >= 15000 -> 10
            xp >= 10000 -> 9
            xp >= 7500 -> 8
            xp >= 5000 -> 7
            xp >= 3200 -> 6
            xp >= 2000 -> 5
            xp >= 1200 -> 4
            xp >= 600 -> 3
            xp >= 250 -> 2
            else -> 1
        }

        // Gather unlocked badges list
        val badges = user.unlockedBadges.split(",").filter { it.isNotBlank() }.toMutableSet()

        // 1. Beginner
        badges.add("Beginner")

        // 2. Consistent
        if (user.currentStreak >= 3) {
            badges.add("Consistent")
        }

        // 3. Discipline King
        if (user.currentStreak >= 7) {
            badges.add("Discipline King")
        }

        // 4. Study Warrior & Focused Student
        val sessions = studySessionDao.getAllSessionsSync()
        if (sessions.isNotEmpty()) {
            badges.add("Focused Student")
        }
        val totalHours = sessions.sumOf { it.durationMinutes } / 60.0f
        if (sessions.size >= 5 || totalHours >= 3.0f) {
            badges.add("Study Warrior")
        }

        // 5. Habit Master
        val habits = habitDao.getAllHabitsSync()
        val totalTicks = habits.sumOf { h ->
            h.completionDates.split(",").filter { it.isNotBlank() }.size
        }
        if (totalTicks >= 10) {
            badges.add("Habit Master")
        }

        // 6. Productivity Champion
        val completedTasks = taskDao.getCompletedTasksSync()
        if (completedTasks.size >= 15) {
            badges.add("Productivity Champion")
        }

        // 7. 30-Day Hero
        val challenges = challengeDao.getAllChallengesSync()
        val hasCompletedChallenge = challenges.any { it.isCompleted || it.completedDays.split(",").filter { d -> d.isNotBlank() }.size >= 15 }
        if (hasCompletedChallenge) {
            badges.add("30-Day Hero")
        }

        // 8. XP Legend
        if (xp >= 2500 || computedLevel >= 5) {
            badges.add("XP Legend")
        }

        val updatedUser = user.copy(
            level = computedLevel,
            unlockedBadges = badges.joinToString(",")
        )
        userDao.insertOrUpdateUser(updatedUser)
    }

    // ==========================================
    // TASK MANAGERS
    // ==========================================

    suspend fun insertTask(title: String, category: String, priority: String, deadline: Long, reminderTime: Long) {
        val reward = when (priority) {
            "High" -> 50
            "Medium" -> 25
            else -> 10
        }
        val task = TaskEntity(
            title = title,
            category = category,
            priority = priority,
            deadline = deadline,
            reminderTime = reminderTime,
            xpReward = reward
        )
        taskDao.insertTask(task)
    }

    suspend fun toggleTaskComplete(taskId: Int) {
        val task = taskDao.getTaskById(taskId) ?: return
        val now = System.currentTimeMillis()
        val newStatus = !task.isCompleted

        val updatedTask = task.copy(
            isCompleted = newStatus,
            completedAt = if (newStatus) now else 0
        )
        taskDao.updateTask(updatedTask)

        if (newStatus) {
            // Reward XP!
            logXpGain(task.xpReward, "Completed Task: ${task.title}")

            // Check if all tasks today completed (+100 XP)
            checkAllTasksCompletedBonus()
        } else {
            // Deduct XP (revert)
            logXpGain(-task.xpReward, "Deducted XP: Reverted Completed Task")
        }
    }

    private suspend fun checkAllTasksCompletedBonus() {
        val allTasks = db.taskDao().getAllTasksSync()
        val currentTasks = allTasks.filter { !it.isCompleted }
        if (currentTasks.isEmpty() && allTasks.isNotEmpty()) {
            // All tasks completed!
            logXpGain(100, "Daily Quest: Perfect Task Day!")
        }
    }

    suspend fun deleteTask(id: Int) {
        taskDao.deleteTaskById(id)
    }

    // ==========================================
    // HABITS
    // ==========================================

    suspend fun insertHabit(name: String) {
        habitDao.insertHabit(HabitEntity(name = name, isCustom = true))
    }

    suspend fun toggleHabitCompletedToday(habitId: Int) {
        val habit = habitDao.getHabitById(habitId) ?: return
        val today = getTodayDateString()
        val completionDates = habit.completionDates.split(",").filter { it.isNotBlank() }.toMutableSet()

        val isNowCompleted = if (completionDates.contains(today)) {
            completionDates.remove(today)
            false
        } else {
            completionDates.add(today)
            true
        }

        val yesterday = getYesterdayDateString()
        var currentStreak = habit.streak
        if (isNowCompleted) {
            if (habit.lastCompletedDate == yesterday) {
                currentStreak++
            } else if (habit.lastCompletedDate != today) {
                currentStreak = 1
            }
        } else {
            // Un-complete today - streak calculation
            if (habit.lastCompletedDate == today) {
                currentStreak = if (currentStreak > 0) currentStreak - 1 else 0
            }
        }

        val updatedHabit = habit.copy(
            completionDates = completionDates.joinToString(","),
            lastCompletedDate = if (isNowCompleted) today else (if (completionDates.contains(yesterday)) yesterday else ""),
            streak = currentStreak
        )
        habitDao.updateHabit(updatedHabit)

        if (isNowCompleted) {
            logXpGain(15, "Completed Habit: ${habit.name}")
            checkAllHabitsCompletedBonus()
        } else {
            logXpGain(-15, "Reverted Habit Completion: ${habit.name}")
        }
    }

    private suspend fun checkAllHabitsCompletedBonus() {
        val habits = habitDao.getAllHabitsSync()
        val today = getTodayDateString()
        val allTickedToday = habits.all { h ->
            h.completionDates.split(",").contains(today)
        }
        if (allTickedToday && habits.isNotEmpty()) {
            logXpGain(75, "Daily Quest: Complete All Habits (+75 XP)")
            logXpGain(50, "Perfect Habit Day (+50 XP)")
        }
    }

    suspend fun deleteHabit(id: Int) {
        habitDao.deleteHabitById(id)
    }

    // ==========================================
    // 30-DAY CHALLENGES / MISSIONS
    // ==========================================

    suspend fun insertChallenge(title: String, type: String) {
        challengeDao.insertChallenge(ChallengeEntity(title = title, challengeType = type))
    }

    suspend fun toggleChallengeDayCompleted(challengeId: Int, dayNum: Int) {
        val challenge = challengeDao.getChallengeById(challengeId) ?: return
        val days = challenge.completedDays.split(",").filter { it.isNotBlank() }.toMutableSet()

        val wasAdded = if (days.contains(dayNum.toString())) {
            days.remove(dayNum.toString())
            false
        } else {
            days.add(dayNum.toString())
            true
        }

        val isChallengeFullyComplete = days.size == 30
        val updatedChallenge = challenge.copy(
            completedDays = days.joinToString(","),
            isCompleted = isChallengeFullyComplete
        )
        challengeDao.updateChallenge(updatedChallenge)

        if (wasAdded) {
            logXpGain(20, "Mission Challenge Completed Day $dayNum")
            if (isChallengeFullyComplete && !challenge.rewardClaimed) {
                // Claim Challenge Complete Bonus!
                logXpGain(500, "Mega Quest: Completed 30-Day Mission!")
                challengeDao.updateChallenge(updatedChallenge.copy(rewardClaimed = true))
            }
        } else {
            logXpGain(-20, "Reverted Challenge Day $dayNum")
        }
    }

    suspend fun deleteChallenge(id: Int) {
        challengeDao.deleteChallengeById(id)
    }

    // ==========================================
    // STUDY SESSION MODULE
    // ==========================================

    suspend fun insertStudySession(subject: String, chapter: String, durationMinutes: Int, notes: String, isRevision: Boolean, testScore: Int = -1) {
        val session = StudySessionEntity(
            subject = subject,
            chapter = chapter,
            durationMinutes = durationMinutes,
            notes = notes,
            isRevision = isRevision,
            timestamp = System.currentTimeMillis(),
            testScore = testScore
        )
        studySessionDao.insertSession(session)

        // Award Study Session XP
        // 30 Minutes Study = 20 XP; 60 Minutes Study = 50 XP; 120 Minutes Study = 120 XP
        val xpGain = when {
            durationMinutes >= 120 -> 120
            durationMinutes >= 60 -> 50
            durationMinutes >= 30 -> 20
            else -> (durationMinutes * 0.6).toInt() // small proportional amount for quick tasks
        }
        logXpGain(xpGain, "Study Session: Trained $durationMinutes mins in $subject")

        // Score bonus
        if (testScore >= 90) {
            logXpGain(50, "Aced Test Score in $subject: $testScore%")
        } else if (testScore >= 75) {
            logXpGain(25, "Great Test Score in $subject: $testScore%")
        }
    }

    suspend fun deleteStudySession(id: Int) {
        studySessionDao.deleteSessionById(id)
    }

    // ==========================================
    // CALENDAR EVENTS SETUP
    // ==========================================

    suspend fun insertCalendarEvent(title: String, startTime: Long, endTime: Long) {
        val event = CalendarEventEntity(
            title = title,
            startTime = startTime,
            endTime = endTime,
            isGoogleSynced = true
        )
        calendarEventDao.insertCalendarEvent(event)
    }

    suspend fun deleteCalendarEvent(id: Int) {
        calendarEventDao.deleteCalendarEventById(id)
    }

    // Simulate Google Calendar Account Connection & Sync
    suspend fun syncGoogleCalendarAgenda() {
        calendarEventDao.clearAllCalendarEvents()
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis

        val events = listOf(
            CalendarEventEntity(title = "Morning Assembly & Class Teacher Update", startTime = today + 3600000, endTime = today + 5400000, isGoogleSynced = true),
            CalendarEventEntity(title = "Class 9 Ch 4 Geometry Lecture", startTime = today + 7200000, endTime = today + 9000000, isGoogleSynced = true),
            CalendarEventEntity(title = "Science Lab: Practical Work in Chemistry", startTime = today + 10800000, endTime = today + 12600000, isGoogleSynced = true),
            CalendarEventEntity(title = "Computer Programming Basics Revision", startTime = today + 18000000, endTime = today + 19800000, isGoogleSynced = true),
            CalendarEventEntity(title = "Daily Study Hour - Quadratic Equations Session", startTime = today + 25200000, endTime = today + 28800000, isGoogleSynced = true)
        )

        for (e in events) {
            calendarEventDao.insertCalendarEvent(e)
        }
        logXpGain(50, "Google Calendar Successfully Synchronized")
    }

    suspend fun updateUserStudyGoal(hours: Float) {
        val user = userDao.getUserSync() ?: return
        userDao.insertOrUpdateUser(user.copy(studyGoalHours = hours))
        logXpGain(10, "Adjusted daily study goal threshold to $hours hours")
    }

    suspend fun clearUserAndData() {
        userDao.deleteUser()
    }
}
