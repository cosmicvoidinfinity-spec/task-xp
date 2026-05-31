package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.XPViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: XPViewModel = viewModel()
                    val user by viewModel.userState.collectAsState()

                    if (user == null) {
                        // User has not logged details yet: launch onboarding stage
                        OnboardingScreen(viewModel = viewModel)
                    } else {
                        // Launched into the main gaming dashboard layout
                        MainNavigationContainer(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainNavigationContainer(
    viewModel: XPViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    var selectedBottomTab by remember { mutableStateOf("Home") }
    var selectedArsenalSubTab by remember { mutableStateOf("Missions") }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MatteBlack,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .testTag("bottom_nav_bar")
            ) {
                // Bottom Menu Items
                val navItems = listOf(
                    Triple("Home", Icons.Default.Dashboard, "Home"),
                    Triple("Quests", Icons.Default.CheckCircle, "Quests"),
                    Triple("Habits", Icons.Default.Favorite, "Habits"),
                    Triple("Study", Icons.Default.School, "Study"),
                    Triple("Arsenal", Icons.Default.Extension, "Arsenal")
                )

                navItems.forEach { (route, icon, label) ->
                    val isSelected = selectedBottomTab == route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedBottomTab = route },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) TechOrange else MutedTextDark
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isSelected) TechOrange else MutedTextDark
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = TechOrange.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = if (isDarkTheme) listOf(SlateBlack, SolidBlack) else listOf(LightBackground, LightSurface)
                    )
                )
        ) {
            AnimatedContent(
                targetState = selectedBottomTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "bottom_tab_transition_animation"
            ) { targetTab ->
                when (targetTab) {
                    "Home" -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToScreen = { target -> selectedBottomTab = target }
                    )
                    "Quests" -> TaskManagerScreen(viewModel = viewModel)
                    "Habits" -> HabitTrackerScreen(viewModel = viewModel)
                    "Study" -> StudentModeScreen(
                        viewModel = viewModel,
                        onNavigateToVexa = {
                            selectedBottomTab = "Arsenal"
                            selectedArsenalSubTab = "Vexa"
                        }
                    )
                    "Arsenal" -> {
                        // Sliding sub-grid header layout for tactical student items (Missions, Pomodoro, Scoreboard, Vexa, Settings)
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val arsenalTabs = listOf(
                                    Pair("Missions", "Missions"),
                                    Pair("Pomodoro", "Pomodoro"),
                                    Pair("Scoreboard", "Stats"),
                                    Pair("Vexa", "Vexa AI"),
                                    Pair("Settings", "Settings")
                                )

                                arsenalTabs.forEach { (subId, labelText) ->
                                    val isSubSelected = selectedArsenalSubTab == subId
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSubSelected) TechOrange else MatteBlack)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSubSelected) TechOrange else BorderGray,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedArsenalSubTab = subId }
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = labelText,
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Divider(color = BorderGray, thickness = 0.5.dp)

                            Box(modifier = Modifier.weight(1f)) {
                                AnimatedContent(
                                    targetState = selectedArsenalSubTab,
                                    transitionSpec = {
                                        fadeIn() togetherWith fadeOut()
                                    },
                                    label = "arsenal_sub_tab_transition"
                                ) { targetSubTab ->
                                    when (targetSubTab) {
                                        "Missions" -> MissionPlannerScreen(viewModel = viewModel)
                                        "Pomodoro" -> PomodoroScreen(viewModel = viewModel)
                                        "Scoreboard" -> ScoreboardScreen(viewModel = viewModel)
                                        "Vexa" -> VexaAIScreen(viewModel = viewModel)
                                        "Settings" -> SettingsScreen(
                                            viewModel = viewModel,
                                            isDarkTheme = isDarkTheme,
                                            onToggleTheme = { onToggleTheme(it) },
                                            onResetOnboarding = {
                                                // Reset local onboarding State Flow
                                                viewModel.completeOnboarding(
                                                    name = "",
                                                    studentClass = "",
                                                    goalHours = 0f,
                                                    initialHabits = emptyList()
                                                )
                                                // Clean navigation anchors
                                                selectedBottomTab = "Home"
                                                selectedArsenalSubTab = "Missions"
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
