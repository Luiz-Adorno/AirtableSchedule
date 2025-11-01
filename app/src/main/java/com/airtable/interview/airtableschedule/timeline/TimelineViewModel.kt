package com.airtable.interview.airtableschedule.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airtable.interview.airtableschedule.models.Event
import com.airtable.interview.airtableschedule.repositories.EventDataRepository
import com.airtable.interview.airtableschedule.repositories.EventDataRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TimelineViewModel : ViewModel() {
    private val eventDataRepository: EventDataRepository = EventDataRepositoryImpl()

    val uiState: StateFlow<TimelineUiState> = eventDataRepository
        .getTimelineItems()
        .map(::TimelineUiState)
        .stateIn(
            viewModelScope,
            initialValue = TimelineUiState(),
            started = SharingStarted.WhileSubscribed()
        )

    /**
     * Builds a list of lanes, each containing non-overlapping events.
     * This ensures compact horizontal placement on the timeline.
     */
    fun buildLanes(events: List<Event>): List<List<Event>> {
        if (events.isEmpty()) return emptyList()

        val sorted = events.sortedBy { it.startDate }
        val lanes = mutableListOf<MutableList<Event>>()

        for (event in sorted) {
            // Try to place the event in an existing lane
            var placed = false
            for (lane in lanes) {
                val lastEvent = lane.last()
                if (event.startDate.after(lastEvent.endDate)) {
                    lane.add(event)
                    placed = true
                    break
                }
            }
            // If not possible, create a new lane
            if (!placed) {
                lanes.add(mutableListOf(event))
            }
        }

        return lanes
    }
}
