package com.airtable.interview.airtableschedule.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airtable.interview.airtableschedule.repositories.EventDataRepository
import com.airtable.interview.airtableschedule.repositories.EventDataRepositoryImpl
import com.airtable.interview.airtableschedule.models.Event
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.PriorityQueue

class TimelineViewModel : ViewModel() {

    private val eventDataRepository: EventDataRepository = EventDataRepositoryImpl()

    val uiState: StateFlow<TimelineUiState> = eventDataRepository
        .getTimelineItems()
        .map { events ->
            TimelineUiState(buildLanes(events))
        }
        .stateIn(
            viewModelScope,
            initialValue = TimelineUiState(),
            started = SharingStarted.WhileSubscribed()
        )

    /**
     * Builds timeline lanes ensuring that overlapping events are placed on separate rows.
     *
     * Algorithm:
     * 1. Sorts events by start date.
     * 2. Uses an interval partitioning approach with a **min-heap** (PriorityQueue)
     *    to always track which lane finishes earliest.
     * 3. Reuses lanes whose latest event ended before the new event starts.
     *
     * The min-heap ensures that we can efficiently find and update
     * the earliest finishing lane in O(log n), resulting in an overall
     * time complexity of **O(n log n)** for n events — optimal for this problem.
     *
     */
    fun buildLanes(events: List<Event>): List<List<Event>> {
        if (events.isEmpty()) return emptyList()

        // Sort events by start date
        val sorted = events.sortedBy { it.startDate.time }

        // Each lane is a list of events
        val lanes = mutableListOf<MutableList<Event>>()

        // Min-heap: stores (endTime, laneIndex)
        // The lane with the earliest endTime is always on top.
        val pq = PriorityQueue<Pair<Long, Int>>(compareBy { it.first })

        for (event in sorted) {
            val start = event.startDate.time
            val end = event.endDate.time

            // Reuse the lane that finishes first (if it's free)
            val top = pq.peek()
            if (top != null && top.first < start) {
                val (_, laneIdx) = pq.poll()
                lanes[laneIdx].add(event)
                pq.offer(end to laneIdx)
            } else {
                // Create new lane if no available one
                val newIdx = lanes.size
                lanes.add(mutableListOf(event))
                pq.offer(end to newIdx)
            }
        }

        // Sort lanes visually by earliest start for better readability
        val visuallySorted = lanes.sortedBy { lane ->
            lane.minOf { it.startDate.time }
        }

        // Debug print
        println("Optimized + visually sorted timeline lanes:")
        visuallySorted.forEachIndexed { index, lane ->
            val ids = lane.joinToString(", ") { it.id.toString() }
            println("Lane ${index + 1}: [$ids]")
        }

        return visuallySorted
    }
}
