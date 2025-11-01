package com.airtable.interview.airtableschedule.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airtable.interview.airtableschedule.models.Event

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lanes = viewModel.buildLanes(uiState.events)

    TimelineView(lanes)
}

//Displays the timeline with multiple lanes.
@Composable
private fun TimelineView(
    lanes: List<List<Event>>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(lanes) { lane ->
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(lane) { event ->
                    EventBlock(event)
                }
            }
        }
    }
}

//Represents a single event as a block on the timeline.
@Composable
private fun EventBlock(event: Event) {
    val durationMillis = event.endDate.time - event.startDate.time
    val durationDays = (durationMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)

    val widthPerDay = 20.dp
    val blockWidth = maxOf(durationDays * widthPerDay.value).dp

    var clicked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(blockWidth)
            .height(50.dp)
            .background(
                if (clicked)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.primaryContainer
            )
            .clickable {
                clicked = !clicked
                println("Clicked on event: ${event.name}")
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = event.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp) // base padding inside the box
        )
    }
}


