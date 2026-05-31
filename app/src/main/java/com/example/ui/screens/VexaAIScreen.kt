package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AIAnalysisState
import com.example.ui.XPViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VexaAIScreen(viewModel: XPViewModel) {
    val aiAnalysis by viewModel.aiAnalysisState.collectAsState()
    val terminalLogs by viewModel.aiCommandLogs.collectAsState()
    val isRunningCommand by viewModel.aiCommandRunning.collectAsState()

    var selectedMode by remember { mutableStateOf("Advisor") } // "Advisor" or "Terminal"
    var commandInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Quick presets
    val presets = listOf(
        "Add task Revise Physics notes priority High",
        "Add habit Read Class 9 news daily",
        "Add 150 xp for solving boards calculus worksheet",
        "Complete task Revise Physics",
        "Update study goal to 4.5 hours"
    )

    // Automatically trigger Vexa core link upon landing on Advisor mode
    LaunchedEffect(Unit) {
        if (aiAnalysis is AIAnalysisState.Idle) {
            viewModel.triggerVexaAnalysis()
        }
    }

    // Scroll automatically to end when new logs arrive
    LaunchedEffect(terminalLogs.size) {
        if (terminalLogs.isNotEmpty()) {
            listState.animateScrollToItem(terminalLogs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Control Mode Tac-Heads Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CYBERNETIC CO-PILOT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TechOrange
                )
                Text(
                    text = "VEXA INTEL SYS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
            }

            // Cyber-switch toggle selector tabs
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateBlack)
                    .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                    .padding(2.dp)
            ) {
                listOf("Advisor", "Terminal").forEach { mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) TechOrange else Color.Transparent)
                            .clickable { selectedMode = mode }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = mode.uppercase(),
                            color = if (isSelected) SolidBlack else MutedTextDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Divider(color = BorderGray, thickness = 0.5.dp)

        AnimatedContent(
            targetState = selectedMode,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "vexa_mode_transition"
        ) { mode ->
            if (mode == "Advisor") {
                // ---- DIAGNOSTIC ADVISOR REPORT MODE ----
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIAGNOSTIC REPORT MATRIX",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { viewModel.triggerVexaAnalysis() },
                            modifier = Modifier
                                .size(34.dp)
                                .border(1.dp, BorderGray, RoundedCornerShape(17.dp))
                                .background(MatteBlack)
                                .testTag("vexa_refresh_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "re-generate", tint = TechOrange, modifier = Modifier.size(16.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(32.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
                            .background(MatteBlack)
                            .padding(16.dp)
                            .testTag("vexa_report_container")
                    ) {
                        when (val state = aiAnalysis) {
                            is AIAnalysisState.Idle -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Vexa Core idle. Initializing diagnostic system...", color = MutedTextDark, fontSize = 12.sp)
                                }
                            }
                            is AIAnalysisState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(color = TechOrange)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "ESTABLISHING CHRONO ENCRYPTION TRANSMISSION...",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = TechOrange,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "VEXA-AI is calculating study ratios across Mathematics, Science, and Social subjects.",
                                        fontSize = 11.sp,
                                        color = MutedTextDark,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                            is AIAnalysisState.Success -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = state.text,
                                        color = PureWhite,
                                        lineHeight = 22.sp,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            is AIAnalysisState.Error -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "error", tint = Color.Red, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "CONNECTION BLOCKED",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.message,
                                        color = MutedTextDark,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.triggerVexaAnalysis() },
                                        colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite)
                                    ) {
                                        Text("RETRY INTERFACE LINK")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ---- REAL-TIME SYSTEM MANAGER TERMINAL MODE ----
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "QUANTUM CONTROL CONSOLE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhite,
                        letterSpacing = 1.sp
                    )

                    // Monospaced Scrollable Terminal Log View
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                            .background(MatteBlack)
                            .padding(14.dp)
                    ) {
                        if (terminalLogs.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = "Terminal", tint = TechOrange.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "[VEXA-CORE]: Terminal Connection Established.",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = TechOrange.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Send dynamic natural instructions below to alter tasks, XP counters, and daily metrics instantly.",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MutedTextDark,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(terminalLogs) { logLine ->
                                    val isError = logLine.contains("ERROR")
                                    val isSuccess = logLine.contains("SUCCESS") || logLine.startsWith("▶")
                                    Text(
                                        text = logLine,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = if (isError) Color.Red else if (isSuccess) Color(0xFF4CAF50) else TechOrange,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Direct Command Quick Action Prefills
                    Text(
                        text = "QUICK CONFIG MATRIX CHIPS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedTextDark
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateBlack)
                                    .border(0.5.dp, BorderGray, RoundedCornerShape(8.dp))
                                    .clickable { commandInput = preset }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = preset,
                                    color = PureWhite,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Tactical Text Input Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = commandInput,
                            onValueChange = { commandInput = it },
                            placeholder = { Text("Command Vexa: 'Add task Study math'...", fontSize = 12.sp, color = MutedTextDark) },
                            textStyle = TextStyle(color = PureWhite, fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite,
                                focusedBorderColor = TechOrange,
                                unfocusedBorderColor = BorderGray,
                                focusedLabelColor = TechOrange,
                                unfocusedLabelColor = MutedTextDark
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (commandInput.isNotBlank() && !isRunningCommand) {
                                        viewModel.executeQuantumAICommand(commandInput)
                                        commandInput = ""
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("vexa_command_input")
                        )

                        // Submit Launcher Button
                        Button(
                            onClick = {
                                if (commandInput.isNotBlank() && !isRunningCommand) {
                                    viewModel.executeQuantumAICommand(commandInput)
                                    commandInput = ""
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = !isRunningCommand && commandInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TechOrange,
                                disabledContainerColor = SlateBlack,
                                contentColor = SolidBlack,
                                disabledContentColor = MutedTextDark
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("vexa_execute_command_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isRunningCommand) {
                                CircularProgressIndicator(color = SolidBlack, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "Run Command")
                            }
                        }
                    }
                }
            }
        }

        // Tactical Info Footer Info panel
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateBlack),
            border = BorderStroke(0.5.dp, BorderGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Core Info", tint = TechOrange, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (selectedMode == "Advisor") {
                        "VEXA study diagnostics parses study tracks in Mathematics, Science, and Social logs to suggest tactics."
                    } else {
                        "Database Direct Engine executes command tags directly into Room DB. Operations log to console dynamically."
                    },
                    fontSize = 11.sp,
                    color = MutedTextDark,
                    lineHeight = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
