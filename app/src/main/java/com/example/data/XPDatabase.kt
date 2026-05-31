package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// ROOM ENTITIES
// ==========================================

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Class 9 Student",
    val studentClass: String = "Class 9",
    val studyGoalHours: Float = 3.0f,
    val habitGoals: String = "", // Comma-separated custom text
    val totalXp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = "", // "yyyy-MM-dd"
    val unlockedBadges: String = "" // Comma-separated list of values like "Beginner,Study Warrior"
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // School, Study, Homework, YouTube, Personal, Health, Family, Reading
    val priority: String, // Low, Medium, High
    val deadline: Long = 0, // Timestamp
    val isCompleted: Boolean = false,
    val completedAt: Long = 0, // Timestamp
    val reminderTime: Long = 0, // Timestamp
    val xpReward: Int = 10
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isCustom: Boolean = false,
    val streak: Int = 0,
    val lastCompletedDate: String = "", // "yyyy-MM-dd"
    val completionDates: String = "" // Comma-separated dates text: "2026-05-30,2026-05-31"
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val challengeType: String, // Study Challenge, Exercise Challenge, Reading Challenge, No Procrastination Challenge, Custom Challenge
    val startedAt: Long = System.currentTimeMillis(),
    val completedDays: String = "", // Comma separated integers: "1,2,3,4,20" (representing completed days of the 30-day grid)
    val isCompleted: Boolean = false,
    val rewardClaimed: Boolean = false
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String, // Mathematics, Science, Social Science, English, Hindi, Computer
    val chapter: String = "",
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isRevision: Boolean = false,
    val testScore: Int = -1 // -1 means no test score recorded
)

@Entity(tableName = "xp_logs")
data class XpLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Int,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val isGoogleSynced: Boolean = false
)

// ==========================================
// ROOM DAOS
// ==========================================

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, deadline ASC, id DESC")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    suspend fun getCompletedTasksSync(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY id DESC")
    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits")
    suspend fun getAllHabitsSync(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Int): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Int)
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY id DESC")
    fun getAllChallengesFlow(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges")
    suspend fun getAllChallengesSync(): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getChallengeById(id: Int): ChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun deleteChallengeById(id: Int)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions")
    suspend fun getAllSessionsSync(): List<StudySessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)
}

@Dao
interface XpLogDao {
    @Query("SELECT * FROM xp_logs ORDER BY timestamp DESC")
    fun getAllXpLogsFlow(): Flow<List<XpLogEntity>>

    @Query("SELECT * FROM xp_logs")
    suspend fun getAllXpLogsSync(): List<XpLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertXpLog(log: XpLogEntity)
}

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM calendar_events ORDER BY startTime ASC")
    fun getAllCalendarEventsFlow(): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events")
    suspend fun getAllCalendarEventsSync(): List<CalendarEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEvent(event: CalendarEventEntity)

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteCalendarEventById(id: Int)

    @Query("DELETE FROM calendar_events")
    suspend fun clearAllCalendarEvents()
}

// ==========================================
// DATABASE DEF
// ==========================================

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        HabitEntity::class,
        ChallengeEntity::class,
        StudySessionEntity::class,
        XpLogEntity::class,
        CalendarEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class XPDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun xpLogDao(): XpLogDao
    abstract fun calendarEventDao(): CalendarEventDao

    companion object {
        @Volatile
        private var INSTANCE: XPDatabase? = null

        fun getDatabase(context: Context): XPDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XPDatabase::class.java,
                    "xp_student_planner_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
