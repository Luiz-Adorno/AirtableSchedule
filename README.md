# 📅 Airtable Timeline Assignment — Android (Jetpack Compose)

## 🕒 1. How long you spent on the assignment

**~4 hours total**

You can verify the approximate timeline of work progression by checking the **commit timestamps** in the Git history,  
which reflect each completed stage (Etapas 1–5).

---

## 💡 2. What you like about your implementation

- Clean, modular structure separating UI, logic, and data sources.  
- Scalable lane layout using `PriorityQueue` (O n log n efficiency).  
- Smooth timeline rendering that adapts to zoom dynamically.  
- Inline editing integrated naturally without breaking layout.  
- Added proportional **date axis** for clarity and usability beyond the base spec.  
- Code organized with clear separation between **ViewModel** and **Compose layer**.

---

## 🧠 3. What you would change if you were going to do it again

If given more time, I would:

1. **Persist edits and drags** in `ViewModel` (two-way data binding via `StateFlow`).  
2. **Finish the drag & drop feature**, allowing users to reposition events horizontally and automatically update start/end dates.  
   *(Partial logic implemented — functional prototype not completed due to time constraints.)*  
3. **Introduce Kotlin Coroutines for better async management** when handling drag updates, data persistence, and potential backend syncing.  
   Coroutines would improve responsiveness and allow smoother event handling without blocking the UI thread.  
4. **Add Dependency Injection (Hilt)** to better manage lifecycle-aware components like repositories, data sources, and formatters.  
   This improves **testability**, **maintainability**, and decouples ViewModels from direct implementation details.  
5. **Adopt a Clean Architecture structure** by introducing domain-level **UseCases**, for example:  
   - `GetTimelineEventsUseCase()`  
   - `UpdateEventNameUseCase()`  
   - `ShiftEventDatesUseCase()`  
   
   Each UseCase would encapsulate one specific piece of business logic, making the code:
   - **Reusable** across different UI layers (e.g., timeline view, event detail screen).  
   - **Easier to test** with isolated unit tests per UseCase.  
   - **More organized** through clear separation between domain, data, and presentation layers.  
6. Add **drag & drop refinement** with touch thresholding and smooth spring animations.  
7. Implement **lane overlap recomputation** when dates change interactively.  
8. Add **unit tests** for the lane allocator and timeline scaling math.  
9. Create **instrumentation tests** for zoom and scroll behavior to ensure gestures don’t conflict.

---

## 🧩 4. How you made your design decisions

### 🧮 Efficient Lane Allocation (Min-Heap Logic)
**Why use a Min-Heap (`PriorityQueue`) for lane assignment**

Events are sorted by start date, and a **min-heap** keeps track of each lane’s last end date.  
This allows for efficient placement of new events in O(log n) time per insert,  
achieving an overall **O(n log n)** complexity — far more scalable than a naive O(n²) approach,  
which would compare every event pair to detect overlaps.

This makes the solution practical for large datasets (hundreds or thousands of events) without noticeable slowdown.

---

### 🧱 TimelineUiState Refactor
**Design decision:**

The original starter code modeled the timeline as a *flat list of events*.  
Since the challenge explicitly requires grouping non-overlapping events into compact lanes,  
I refactored `TimelineUiState` to hold a list of lanes (`List<List<Event>>`).  

This structural change:
- Improves **separation of concerns**, letting the ViewModel handle all computation (lane assignment, ordering).  
- Keeps the Compose layer focused purely on **UI rendering** and interaction.  
- Enables future extensions (dynamic lane creation, filtering, or re-layout).

---

### 🧭 Added Top-Level Date Axis
Although not required in the assignment, I introduced a **top-level date axis** that displays evenly spaced date labels (every 5 days),  
aligned proportionally to the actual timeline scale.

**Reasoning:**
- Greatly improves **user orientation** when zooming in/out.  
- Helps visually validate that events are truly positioned by real chronological distance.  
- Provides a more intuitive experience for longer timelines.

---

### 📝 Inline Editing and Local State
Inline editing was implemented using a `TextField` inside each event block.  
Users can tap to edit the event name directly on the timeline.

**However:**
> Edits are stored locally in composable memory (`remember`).  
> Changes are not yet persisted in the ViewModel or repository.  
> Future updates would route name and date changes through the ViewModel for reactive, persistent state management.

This was intentional, as the assignment didn’t require data persistence —  
but it would be a natural next step for production readiness.

---

### 🎚️ Zoom Functionality
A **Slider** controls zoom level, adjusting the “dp per day” scale in real time.  
This allows users to zoom in for detail or out for an overview.

Combined with the proportional date axis, this provides a clear visual understanding of event spacing and duration.

---

## 🧪 5. How you would test this if you had more time

If I had more time, I would test in three levels:

### 1️⃣ Unit tests
- Verify lane distribution logic with overlapping and non-overlapping events.  
- Ensure Min-Heap ordering yields correct lane reuse.  
- Validate proportional width scaling (duration → pixels).

### 2️⃣ UI tests (Compose)
- Simulate editing event names and ensure layout updates without recomposition errors.  
- Test zoom slider responsiveness and edge cases (min/max zoom).  

### 3️⃣ Integration tests
- Mock repository data and validate full UI rendering of multiple lanes under various zoom levels.

---

## ⚙️ 6. Any special instructions on how to build/run your app

1. Clone the repository and open it in **Android Studio (Giraffe or newer)**.  
2. Let Gradle sync dependencies automatically.  
3. Run the app on an **emulator or device (API 24+)**.  
4. The main timeline is located in:
com.airtable.interview.airtableschedule.timeline.TimelineScreen


No extra setup is required — the project runs out of the box using the provided `SampleTimelineItems.kt` data.

---

## 🧩 Architecture Summary

| Layer | Responsibility |
|-------|----------------|
| **ViewModel (`TimelineViewModel`)** | Fetches data, builds lane structure with Min-Heap algorithm. |
| **UI (`TimelineScreen`)** | Renders lanes and events, handles zoom, inline editing, and visuals. |
| **Repository (`EventDataRepositoryImpl`)** | Provides sample event data. |

---

## 🧭 Conclusion

This implementation delivers a clear, maintainable, and scalable timeline visualization that adheres to the challenge’s requirements while introducing subtle UX improvements.

- 🚀 **Efficient O(n log n) lane assignment**
- ✏️ **Inline editing with Compose-native state**
- 📏 **Proportional zoom and date axis**
- 🧩 **Well-structured ViewModel separation**
- 🕒 **~4 hours of total implementation time**
- ⚙️ **Drag & Drop planned, partially explored but not finalized due to time**
