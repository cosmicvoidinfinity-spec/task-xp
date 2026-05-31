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
}
