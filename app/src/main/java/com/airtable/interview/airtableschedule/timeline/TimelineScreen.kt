package com.airtable.interview.airtableschedule.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airtable.interview.airtableschedule.models.Event
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.TextStyle


@Composable
fun TimelineScreen(viewModel: TimelineViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TimelineView(lanes = uiState.lanes)
}

/**
 * Renders lanes and events proportionally to their real dates.
 * Each event's position on the X-axis represents its start date.
 * Its width represents its duration in days.
 */
@Composable
fun TimelineView(lanes: List<List<Event>>) {
    var widthPerDay by remember { mutableFloatStateOf(10f) }
    val sharedScrollState = rememberScrollState()

    val laneColors = listOf(
        Color(0xFF6FA8DC), Color(0xFF93C47D),
        Color(0xFFE06666), Color(0xFFF6B26B),
        Color(0xFF8E7CC3), Color(0xFF76A5AF)
    )

    val allEvents = lanes.flatten()
    val minStart = allEvents.minOfOrNull { it.startDate } ?: return
    val maxEnd = allEvents.maxOfOrNull { it.endDate } ?: return

    val totalDays = TimeUnit.MILLISECONDS.toDays(maxEnd.time - minStart.time).toInt()
    val dateFormat = SimpleDateFormat("MMM dd", Locale.US)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Zoom control
        Slider(
            value = widthPerDay,
            onValueChange = { widthPerDay = it },
            valueRange = 5f..120f,
            steps = 15,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Single horizontally scrollable region containing dates + lanes
        Box(
            modifier = Modifier
                .horizontalScroll(sharedScrollState)
                .fillMaxWidth()
        ) {
            Column {
                // --- Date axis ---
                Row(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                ) {
                    val labelStep = 5
                    val totalLabels = (totalDays / labelStep) + 1
                    repeat(totalLabels) { i ->
                        val date = Calendar.getInstance().apply {
                            time = minStart
                            add(Calendar.DAY_OF_YEAR, i * labelStep)
                        }.time
                        Text(
                            text = dateFormat.format(date),
                            color = Color.DarkGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.width((labelStep * widthPerDay).dp)
                        )
                    }
                }

                // --- Event lanes ---
                lanes.forEachIndexed { laneIndex, lane ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        lane.forEachIndexed { index, event ->
                            val offsetDays =
                                TimeUnit.MILLISECONDS.toDays(event.startDate.time - minStart.time)
                            val durationDays =
                                TimeUnit.MILLISECONDS.toDays(event.endDate.time - event.startDate.time)
                            val duration = durationDays.toInt().coerceAtLeast(1)

                            // Offset before first event
                            if (index == 0) {
                                Spacer(
                                    modifier = Modifier.width((offsetDays * widthPerDay).dp)
                                )
                            }

                            // --- Event block with inline editing ---
                            Box(
                                modifier = Modifier
                                    .width((duration * widthPerDay).dp)
                                    .height(70.dp)
                                    .background(
                                        color = laneColors[laneIndex % laneColors.size],
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                            6.dp
                                        )
                                    )
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Local state for inline editing
                                var isEditing by remember { mutableStateOf(false) }
                                var editedName by remember { mutableStateOf(event.name) }

                                val dateFormat = SimpleDateFormat("MMM dd", Locale.US)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isEditing) {
                                        TextField(
                                            value = editedName,
                                            onValueChange = { editedName = it },
                                            singleLine = true,
                                            textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                                                cursorColor = Color.White
                                            ),
                                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                            keyboardActions = KeyboardActions(onDone = { isEditing = false })
                                        )
                                    } else {
                                        Text(
                                            text = editedName,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isEditing = true } // enter edit mode
                                        )
                                    }

                                    Text(
                                        text = "${dateFormat.format(event.startDate)} - ${dateFormat.format(event.endDate)}",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
