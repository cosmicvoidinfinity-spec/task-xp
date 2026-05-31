package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface AIAnalysisState {
    object Idle : AIAnalysisState
    object Loading : AIAnalysisState
    data class Success(val text: String) : AIAnalysisState
    data class Error(val message: String) : AIAnalysisState
}

class XPViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = XPRepository(application)

    // State Flows pulled from Room with reactive updates
    val userState: StateFlow<UserEntity?> = repository.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tasksState: StateFlow<List<TaskEntity>> = repository.allTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habitsState: StateFlow<List<HabitEntity>> = repository.allHabitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val challengesState: StateFlow<List<ChallengeEntity>> = repository.allChallengesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessionsState: StateFlow<List<StudySessionEntity>> = repository.allSessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val xpLogsState: StateFlow<List<XpLogEntity>> = repository.allXpLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarEventsState: StateFlow<List<CalendarEventEntity>> = repository.allCalendarEventsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI diagnostic State Flow
    private val _aiAnalysis = MutableStateFlow<AIAnalysisState>(AIAnalysisState.Idle)
    val aiAnalysisState: StateFlow<AIAnalysisState> = _aiAnalysis.asStateFlow()

    init {
        // Automatically verify login and streak integrity upon launching the tactical grid
        viewModelScope.launch {
            repository.checkDailyStreak()
        }
    }

    // ==========================================
    // ONBOARDING SETUP ACTION
    // ==========================================

    fun completeOnboarding(name: String, studentClass: String, goalHours: Float, initialHabits: List<String>) {
        viewModelScope.launch {
            repository.setupStarterOnboarding(name, studentClass, goalHours, initialHabits)
        }
    }

    // ==========================================
    // GAMIFICATION MECHANICS & XP INJECTIONS
    // ==========================================

    fun addManualXp(amount: Int, reason: String) {
        viewModelScope.launch {
            repository.logXpGain(amount, reason)
        }
    }

    // ==========================================
    // TASK CONSTRUCTORS & CRUD
    // ==========================================

    fun createTask(title: String, category: String, priority: String, deadline: Long = 0, reminderTime: Long = 0) {
        viewModelScope.launch {
            repository.insertTask(title, category, priority, deadline, reminderTime)
        }
    }

    fun toggleTask(taskId: Int) {
        viewModelScope.launch {
            repository.toggleTaskComplete(taskId)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    // ==========================================
    // HABITS
    // ==========================================

    fun createHabit(name: String) {
        viewModelScope.launch {
            repository.insertHabit(name)
        }
    }

    fun toggleHabit(habitId: Int) {
        viewModelScope.launch {
            repository.toggleHabitCompletedToday(habitId)
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    // ==========================================
    // 30-DAY MISSION / GRID LOGISTICS
    // ==========================================

    fun createChallenge(title: String, challengeType: String) {
        viewModelScope.launch {
            repository.insertChallenge(title, challengeType)
        }
    }

    fun tickChallengeDay(challengeId: Int, dayNum: Int) {
        viewModelScope.launch {
            repository.toggleChallengeDayCompleted(challengeId, dayNum)
        }
    }

    fun deleteChallenge(challengeId: Int) {
        viewModelScope.launch {
            repository.deleteChallenge(challengeId)
        }
    }

    // ==========================================
    // STUDY SESSION MODULE
    // ==========================================

    fun recordStudySession(subject: String, chapter: String, durationMinutes: Int, notes: String, isRevision: Boolean, testScore: Int = -1) {
        viewModelScope.launch {
            repository.insertStudySession(subject, chapter, durationMinutes, notes, isRevision, testScore)
        }
    }

    fun deleteStudySession(sessionId: Int) {
        viewModelScope.launch {
            repository.deleteStudySession(sessionId)
        }
    }

    // ==========================================
    // POMODORO TIMER CORE
    // ==========================================

    fun awardPomodoroCompletion(durationMinutes: Int) {
        viewModelScope.launch {
            val xpReward = when (durationMinutes) {
                25 -> 20
                50 -> 50
                else -> (durationMinutes * 0.8).toInt()
            }
            repository.logXpGain(xpReward, "Completed Pomodoro Focus Session ($durationMinutes mins)")
            // Also catalog it inside study sessions under Pomodoro block!
            repository.insertStudySession(
                subject = "Computer", // computer/pomodoro focus work
                chapter = "Pomodoro Focus Grind",
                durationMinutes = durationMinutes,
                notes = "Completed deep study sprint with Pomodoro focus clock.",
                isRevision = false
            )
        }
    }

    // ==========================================
    // GOOGLE CALENDAR & SYNC
    // ==========================================

    fun syncGoogleCalendar() {
        viewModelScope.launch {
            repository.syncGoogleCalendarAgenda()
        }
    }

    // ==========================================
    // AI PRODUCTIVITY ADVICE REQUESTS
    // ==========================================

    fun triggerVexaAnalysis() {
        val user = userState.value ?: return
        val tasks = tasksState.value
        val habits = habitsState.value
        val sessions = sessionsState.value

        _aiAnalysis.value = AIAnalysisState.Loading

        viewModelScope.launch {
            try {
                val results = AIService.generateProductivityAnalysis(
                    userName = user.name,
                    studentClass = user.studentClass,
                    userLevel = user.level,
                    totalXp = user.totalXp,
                    dailyHoursGoal = user.studyGoalHours,
                    completedTasksCount = tasks.count { it.isCompleted },
                    pendingTasksCount = tasks.count { !it.isCompleted },
                    habitsList = habits,
                    sessionsList = sessions
                )
                _aiAnalysis.value = AIAnalysisState.Success(results)
            } catch (e: Exception) {
                _aiAnalysis.value = AIAnalysisState.Error(e.localizedMessage ?: "Critical quantum link failure with Vexa core.")
            }
        }
    }

    // AI DIRECT-COMMAND TERMINAL CORE
    val aiCommandRunning = MutableStateFlow(false)
    val aiCommandLogs = MutableStateFlow<List<String>>(emptyList())

    fun executeQuantumAICommand(prompt: String) {
        if (prompt.isBlank()) return
        aiCommandRunning.value = true
        aiCommandLogs.value = listOf(
            "[VEXA TERMINAL]: Launching cybernetic database link...",
            "[VEXA TERMINAL]: Processing instruction: \"$prompt\""
        )

        viewModelScope.launch {
            try {
                val rawResult = repository.run { 
                    // Call Direct AI command module
                    AIService.executeAiCommand(prompt) 
                }
                
                aiCommandLogs.value = aiCommandLogs.value + "[VEXA TERMINAL]: Parsing telemetry telemetry payload..."

                // Extract substring between first '[' and last ']' to handle any markdown/conversational formatting cleanly
                var cleanedJson = rawResult.trim()
                val startIdx = cleanedJson.indexOf('[')
                val endIdx = cleanedJson.lastIndexOf(']')
                if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                    cleanedJson = cleanedJson.substring(startIdx, endIdx + 1)
                }

                aiCommandLogs.value = aiCommandLogs.value + "[VEXA TERMINAL]: Executing directives..."

                val jsonArray = org.json.JSONArray(cleanedJson)
                if (jsonArray.length() == 0) {
                    aiCommandLogs.value = aiCommandLogs.value + "[VEXA WARNING]: Empty execution package received."
                }

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val action = obj.getString("action")

                    when (action) {
                        "add_task" -> {
                            val title = obj.getString("title")
                            val cat = if (obj.has("category")) obj.getString("category") else "Study"
                            val prio = if (obj.has("priority")) obj.getString("priority") else "Medium"
                            repository.insertTask(title, cat, prio, 0, 0)
                            aiCommandLogs.value = aiCommandLogs.value + "▶ CREATE TASK: \"$title\" | Cat: $cat | Priority: $prio"
                        }
                        "add_habit" -> {
                            val name = obj.getString("name")
                            repository.insertHabit(name)
                            aiCommandLogs.value = aiCommandLogs.value + "▶ CREATE HABIT: \"$name\""
                        }
                        "add_xp" -> {
                            val amt = obj.getInt("amount")
                            val reason = if (obj.has("reason")) obj.getString("reason") else "Matrix Overcharge"
                            repository.logXpGain(amt, reason)
                            aiCommandLogs.value = aiCommandLogs.value + "▶ XP GRANTED: +$amt XP for \"$reason\""
                        }
                        "update_study_goal" -> {
                            val hours = obj.getDouble("hours").toFloat()
                            repository.updateUserStudyGoal(hours)
                            aiCommandLogs.value = aiCommandLogs.value + "▶ STUDY METRIC CHANGED: Daily hours set to $hours"
                        }
                        "create_challenge" -> {
                            val title = obj.getString("title")
                            val type = if (obj.has("challengeType")) obj.getString("challengeType") else "Syllabus Board Master"
                            repository.insertChallenge(title, type)
                            aiCommandLogs.value = aiCommandLogs.value + "▶ LAUNCH 30-DAY MISSION: \"$title\""
                        }
                        "complete_task" -> {
                            val titleKeyword = obj.getString("title")
                            val match = tasksState.value.firstOrNull { 
                                it.title.contains(titleKeyword, ignoreCase = true) && !it.isCompleted 
                            }
                            if (match != null) {
                                repository.toggleTaskComplete(match.id)
                                aiCommandLogs.value = aiCommandLogs.value + "▶ QUEST CHECKED: Completed \"${match.title}\""
                            } else {
                                aiCommandLogs.value = aiCommandLogs.value + "▶ ERROR: Task matching \"$titleKeyword\" not found or already done."
                            }
                        }
                        "complete_habit" -> {
                            val nameKeyword = obj.getString("name")
                            val match = habitsState.value.firstOrNull { 
                                it.name.contains(nameKeyword, ignoreCase = true) 
                            }
                            if (match != null) {
                                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                                if (!match.completionDates.split(",").contains(today)) {
                                    repository.toggleHabitCompletedToday(match.id)
                                    aiCommandLogs.value = aiCommandLogs.value + "▶ HABIT SECURED: Completed daily habit \"${match.name}\""
                                } else {
                                    aiCommandLogs.value = aiCommandLogs.value + "▶ HABIT ALREADY SECURED: \"${match.name}\""
                                }
                            } else {
                                aiCommandLogs.value = aiCommandLogs.value + "▶ ERROR: Habit matching \"$nameKeyword\" not found."
                            }
                        }
                        else -> {
                            aiCommandLogs.value = aiCommandLogs.value + "▶ WARNING: Action \"$action\" ignored."
                        }
                    }
                }
                aiCommandLogs.value = aiCommandLogs.value + "[VEXA TERMINAL]: Transaction logged. Database updated successfully."
            } catch (e: Exception) {
                e.printStackTrace()
                aiCommandLogs.value = aiCommandLogs.value + "[VEXA TERMINAL ERROR]: Link disrupted: ${e.localizedMessage}"
            } finally {
                aiCommandRunning.value = false
            }
        }
    }

    fun destructAvatarProfile() {
        viewModelScope.launch {
            repository.clearUserAndData()
        }
    }
}
