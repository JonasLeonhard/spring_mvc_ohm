package pojos

import models.Invitation
import java.time.LocalDate

data class CalendarTimeline(
        val localDate: LocalDate? = null,
        val timelineEvents: MutableList<CalendarTimelineEvent>? = null,
        val annotations: MutableList<CalendarTimelineAnnotation>? = null
)

data class CalendarTimelineAnnotation(
        val display: Boolean,
        val annotation: String,
        var gridRowStart: Int
)

data class CalendarTimelineEvent(
        val invitation: Invitation,
        val gridRowStart: Int? = null,
        val gridRowEnd: Int? = null
)