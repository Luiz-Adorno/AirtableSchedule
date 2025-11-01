package com.airtable.interview.airtableschedule.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airtable.interview.airtableschedule.models.Event
import java.util.concurrent.TimeUnit

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
    val widthPerDay = 10.dp

    // Define color palette for lanes
    val laneColors = listOf(
        Color(0xFF6FA8DC), // blue
        Color(0xFF93C47D), // green
        Color(0xFFE06666), // red
        Color(0xFFF6B26B), // orange
        Color(0xFF8E7CC3), // purple
        Color(0xFF76A5AF), // teal
    )

    // Find global min start date for consistent alignment
    val minStart = lanes.flatten().minOfOrNull { it.startDate } ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        itemsIndexed(lanes) { laneIndex, lane ->
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                itemsIndexed(lane) { index, event ->
                    // Compute offset (gap from global start)
                    val offsetDays =
                        TimeUnit.MILLISECONDS.toDays(event.startDate.time - minStart.time)

                    // Compute width based on duration
                    val durationDays =
                        TimeUnit.MILLISECONDS.toDays(event.endDate.time - event.startDate.time)

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // Add horizontal offset only for the first event in the lane
                        if (index == 0) {
                            Spacer(
                                modifier = Modifier.width((offsetDays.toFloat() * widthPerDay.value).dp)
                            )

                        }

                        // Draw event block
                        Box(
                            modifier = Modifier
                                .width((durationDays.toFloat() * widthPerDay.value).dp)
                                .height(40.dp)
                                .background(
                                    color = laneColors[laneIndex % laneColors.size],
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Display event name truncated if too long
                            Text(
                                text = event.name,
                                color = Color.White,
                                fontSize = 12.sp,
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
