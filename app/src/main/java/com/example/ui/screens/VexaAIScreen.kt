package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AIAnalysisState
import com.example.ui.XPViewModel
import com.example.ui.theme.*

@Composable
fun VexaAIScreen(viewModel: XPViewModel) {
    val aiAnalysis by viewModel.aiAnalysisState.collectAsState()

    // Automatically trigger Vexa core link upon landing on this screen!
    LaunchedEffect(Unit) {
        if (aiAnalysis is AIAnalysisState.Idle) {
            viewModel.triggerVexaAnalysis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI diagnostics Header block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🤖 VEXA INTERFACE SYLLABUS UNIT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = TechOrange
                )
                Text(
                    text = "Tactical Gemini-API study diagnostics analyzer.",
                    fontSize = 12.sp,
                    color = MutedTextDark
                )
            }
            IconButton(
                onClick = { viewModel.triggerVexaAnalysis() },
                modifier = Modifier
                    .border(1.dp, TechOrange, RoundedCornerShape(12.dp))
                    .background(MatteBlack)
                    .testTag("vexa_refresh_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "re-generate", tint = TechOrange)
            }
        }

        Divider(color = BorderGray, thickness = 0.5.dp)

        // Main Report Body Scroll view
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, BorderGray, RoundedCornerShape(20.dp))
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
                        Spacer(modifier = Modifier.height(24.dp))
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

        // Diagnostic Footer Info panel
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateBlack),
            border = BorderStroke(0.5.dp, BorderGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Key Status", tint = TechOrange, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "VEXA operates on standard Gemini Direct Flash models. Configure keys under Google AI studio panel presets.",
                    fontSize = 11.sp,
                    color = MutedTextDark,
                    lineHeight = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
