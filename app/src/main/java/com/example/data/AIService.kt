package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

// ==========================================
// GEMINI API PAYLOAD MODELS
// ==========================================

@JsonClass(generateAdapter = true)
data class Part(
    val text: String?
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = 0.7f,
    val maxOutputTokens: Int? = 1024
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

// ==========================================
// RETROFIT CLIENT CONFIGURATION
// ==========================================

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Body request: GenerateContentRequest,
        @Query("key") apiKey: String = BuildConfig.GEMINI_API_KEY
    ): GenerateContentResponse
}

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}

// ==========================================
// AI PRODUCTIVITY ANALYZER SERVICE
// ==========================================

object AIService {
    suspend fun generateProductivityAnalysis(
        userName: String,
        studentClass: String,
        userLevel: Int,
        totalXp: Int,
        dailyHoursGoal: Float,
        completedTasksCount: Int,
        pendingTasksCount: Int,
        habitsList: List<HabitEntity>,
        sessionsList: List<StudySessionEntity>
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return generateMockAnalysis(userName, studentClass, userLevel, totalXp, dailyHoursGoal, completedTasksCount, pendingTasksCount, habitsList, sessionsList)
        }

        // Aggregate statistics for prompt construction
        val totalStudyMinutes = sessionsList.sumOf { it.durationMinutes }
        val sessionsBySubject = sessionsList.groupBy { it.subject }
        val subjectStatsText = sessionsBySubject.map { (subject, list) ->
            "- $subject: ${list.sumOf { it.durationMinutes }} mins (${list.size} sessions)"
        }.joinToString("\n")

        val completedHabitsText = habitsList.map { h ->
            val daysCount = h.completionDates.split(",").filter { it.isNotBlank() }.size
            "- ${h.name}: Checked $daysCount times (Current Streak: ${h.streak} days)"
        }.joinToString("\n")

        val prompt = """
            You are "VEXA", the gamified XP study system coaching engine for Class 9 Student Planner.
            Analyze the following student metrics and generate a concise, highly gamified, extremely motivating critique, study plan adjustment, and daily quest recommendation.
            
            --- STUDENT DOSSIER ---
            Student Name: $userName
            Target Level: $studentClass
            Current Level: Level $userLevel (Total XP: $totalXp XP)
            Daily Study Goal: $dailyHoursGoal hours
            
            --- METRICS & PERFORMANCE ---
            - Tasks Completed: $completedTasksCount (Pending Tasks: $pendingTasksCount)
            - Total Cumulative Study Time: ${totalStudyMinutes / 60}h ${totalStudyMinutes % 60}m
            - Subject Breakdown:
            $subjectStatsText
            
            - Habits Consistency:
            $completedHabitsText
            
            --- INSTRUCTIONS FOR RESPONSE ---
            - Maintain an encouraging, futuristic, gamified tactical tone (like an AI companion in a space RPG).
            - Highlight any clear successes (e.g. high streak counts or completed goals).
            - Identify weak areas (e.g. subjects with 0 study minutes or habits with low completion rates).
            - Give 3 specific actionable level-up keys tailored for a Class 9 student.
            - Keep it short, structured, engaging, and in markdown.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are Vexa, an advanced student productivity AI coach. Provide high-impact, actionable study planner advice using cool RPG status screens and direct call-to-actions.")))
        )

        return try {
            val response = RetrofitClient.apiService.generateContent(request, apiKey)
            val output = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            output ?: "VEXA interface offline. Failed to receive transmission. Please check your network connection."
        } catch (e: Exception) {
            e.printStackTrace()
            "Error contact with VEXA core server: ${e.localizedMessage}. Generating simulated dashboard preview below:\n\n" +
                    generateMockAnalysis(userName, studentClass, userLevel, totalXp, dailyHoursGoal, completedTasksCount, pendingTasksCount, habitsList, sessionsList)
        }
    }

    private fun generateMockAnalysis(
        userName: String,
        studentClass: String,
        userLevel: Int,
        totalXp: Int,
        dailyHoursGoal: Float,
        completedTasksCount: Int,
        pendingTasksCount: Int,
        habitsList: List<HabitEntity>,
        sessionsList: List<StudySessionEntity>
    ): String {
        val totalMins = sessionsList.sumOf { it.durationMinutes }
        val mathMins = sessionsList.filter { it.subject == "Mathematics" }.sumOf { it.durationMinutes }
        val scienceMins = sessionsList.filter { it.subject == "Science" }.sumOf { it.durationMinutes }

        return """
            ### 🤖 VEXA CHRONO DIAGNOSTIC BRIEF
            Greetings, **$userName**! Your Level **$userLevel** Student stats have been analyzed in our tactical matrix.
            
            #### 📊 CURRENT TRAJECTORY
            *   **Quest Progress**: Completed **$completedTasksCount** critical tasks. Your pending item count is **$pendingTasksCount**.
            *   **Focus Threshold**: Cumulative study registers at **${totalMins / 60} hours and ${totalMins % 60} minutes**.
            *   **Habit Heatmap**: Your habit consistency index is active! Excellent work maintaining streaks across default targets.
            
            #### ⚠️ WEAKNESS VECTORS
            ${if (mathMins < 45) "*   **Cognitive Drop**: Mathematics revision is below peak performance ($mathMins mins). Engage Math Chapters to stabilize test scores." else ""}
            ${if (scienceMins < 45) "*   **Lab Deficiency**: Science studies are trending low ($scienceMins mins). Double up on biology/chemistry notes." else ""}
            *   **Habit Gaps**: Ensure custom triggers are activated early to secure the perfect days!
            
            #### 🚀 LEVEL-UP ACTION ITEMS (CLASS 9 BOARD PREP)
            1.  **Algebra Overcharge**: Schedule 30 minutes of Mathematics quadratic formulas to grab a +20 XP bonus.
            2.  **Habit Fortify**: Claim a perfect day tomorrow by ticking off **all 6 active habits** early to stack a **+50 XP Perfect Habit Day bonus**.
            3.  **VEXA Quest**: Initiate a **30-Day Study Challenge** to unlock the **30-Day Hero Badge** and secure **+500 mega XP**.
        """.trimIndent()
    }
}
