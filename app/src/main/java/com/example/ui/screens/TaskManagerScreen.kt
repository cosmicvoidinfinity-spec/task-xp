package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.XPViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskManagerScreen(viewModel: XPViewModel) {
    val tasks by viewModel.tasksState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskCategory by remember { mutableStateOf("Study") }
    var taskPriority by remember { mutableStateOf("Medium") }

    val categories = listOf("School", "Study", "Homework", "YouTube", "Personal", "Health", "Family", "Reading")
    val priorities = listOf("Low", "Medium", "High")

    var selectedTabFilter by remember { mutableStateOf("Pending") }

    val filteredTasks = tasks.filter {
        if (selectedTabFilter == "Pending") !it.isCompleted
        else it.isCompleted
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = TechOrange,
                contentColor = PureWhite,
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Task List Header
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "ACADEMIC WARRIOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = TechOrange
                )
                Text(
                    text = "MISSIONS & QUESTS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                )
            }

            // Segmented Filters Pending vs Completed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, BorderGray, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MatteBlack)
            ) {
                listOf("Pending", "Completed").forEach { tab ->
                    val isSelected = selectedTabFilter == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTabFilter = tab }
                            .background(if (isSelected) TechOrange else Color.Transparent)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) PureWhite else MutedTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Empty tasks",
                            tint = MutedTextDark,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No quests logged in this category.",
                            color = MutedTextDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap the [+] button to forge a new mission.",
                            fontSize = 12.sp,
                            color = MutedTextDark
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                            colors = CardDefaults.cardColors(containerColor = MatteBlack)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { viewModel.toggleTask(task.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = TechOrange,
                                        uncheckedColor = MutedTextDark
                                    )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Priority Badge
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = when (task.priority) {
                                                    "High" -> Color(0xFFD32F2F).copy(alpha = 0.15f)
                                                    "Medium" -> Color(0xFFF57C00).copy(alpha = 0.15f)
                                                    else -> Color(0xFF388E3C).copy(alpha = 0.15f)
                                                }
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${task.priority} Priority",
                                                color = when (task.priority) {
                                                    "High" -> Color(0xFFF44336)
                                                    "Medium" -> Color(0xFFFF9800)
                                                    else -> Color(0xFF4CAF50)
                                                },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        // Category Badge
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = TechOrange.copy(alpha = 0.1f)),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = task.category,
                                                color = TechOrange,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = "Reward: +${task.xpReward} XP",
                                            color = MutedTextDark,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteTask(task.id) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete task",
                                        tint = MutedTextDark.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Task Creation Dialog Box
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = MatteBlack,
            title = {
                Text(
                    "CONSTRUCT NEW QUEST",
                    fontWeight = FontWeight.Black,
                    color = TechOrange,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Quest Title / Objective") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = TechOrange,
                            unfocusedBorderColor = BorderGray,
                            focusedLabelColor = TechOrange,
                            unfocusedLabelColor = MutedTextDark
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_task_title_input")
                    )

                    // Category Selector Label
                    Text("Select Category Module:", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { cat ->
                            val isSelected = taskCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TechOrange else SlateBlack)
                                    .clickable { taskCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(cat, color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Priority Selector Label
                    Text("Combat Priority Factor:", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        priorities.forEach { prio ->
                            val isSelected = taskPriority == prio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TechOrange else SlateBlack)
                                    .clickable { taskPriority = prio }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(prio, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            viewModel.createTask(
                                title = taskTitle,
                                category = taskCategory,
                                priority = taskPriority
                            )
                            taskTitle = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechOrange, contentColor = PureWhite),
                    modifier = Modifier.testTag("submit_add_task_button")
                ) {
                    Text("FORGE CONFLICT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("ABORT", color = MutedTextDark)
                }
            }
        )
    }
}
