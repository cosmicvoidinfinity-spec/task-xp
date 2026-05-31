package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.XPViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen(viewModel: XPViewModel) {
    var maxSeconds by remember { mutableStateOf(25 * 60) }
    var secondsRemaining by remember { mutableStateOf(maxSeconds) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Tick handle
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
            }
            if (secondsRemaining == 0) {
                // Award XP!
                viewModel.awardPomodoroCompletion(maxSeconds / 60)
                isTimerRunning = false
                secondsRemaining = maxSeconds
            }
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val circleProgress = if (maxSeconds > 0) secondsRemaining.toFloat() / maxSeconds else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Pomodoro header
        Text(
            text = "⏱️ CYCLIC FOCUS CLOCK",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = TechOrange
        )
        Text(
            text = "Initiate a 25-minute study sprint. Completing a full Focus Cycle grants +20 XP instantly.",
            fontSize = 12.sp,
            color = MutedTextDark,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Custom Canvas drawing for immersive progress countdown glow arc!
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(MatteBlack, CircleShape)
                .border(1.dp, BorderGray, CircleShape)
                .testTag("pomodoro_clock_container"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                // Background circle ring
                drawCircle(
                    color = BorderGray,
                    style = Stroke(width = 8.dp.toPx())
                )
                // Colored active arc mapping timer countdown!
                drawArc(
                    color = TechOrange,
                    startAngle = -90f,
                    sweepAngle = 360f * circleProgress,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 42.sp,
                    color = PureWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isTimerRunning) "GRID FOCUS ACTIVE" else "IDLE COOLDOWN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isTimerRunning) TechOrange else MutedTextDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reset button
            Button(
                onClick = {
                    isTimerRunning = false
                    secondsRemaining = maxSeconds
                },
                colors = ButtonDefaults.buttonColors(containerColor = BorderGray, contentColor = PureWhite),
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "reset", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESET", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Start/Pause button
            Button(
                onClick = { isTimerRunning = !isTimerRunning },
                colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite),
                modifier = Modifier.weight(1.5f).height(48.dp).testTag("pomodoro_start_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "start-pause",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTimerRunning) "PAUSE SPRINT" else "LAUNCH SPRINT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick session selectors
        Text("SELECT STAGE DURATION LEVEL:", color = MutedTextDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(5, 15, 25, 45).forEach { mins ->
                val secondsValue = mins * 60
                val isSelected = maxSeconds == secondsValue
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) TechOrange else MatteBlack)
                        .border(1.dp, if (isSelected) TechOrange else BorderGray, RoundedCornerShape(12.dp))
                        .clickable {
                            isTimerRunning = false
                            maxSeconds = secondsValue
                            secondsRemaining = secondsValue
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$mins m",
                        color = PureWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
