package pojos

import models.Invitation
import java.time.LocalDate

data class CalendarTimeline(
        val localDate: LocalDate? = null,
        val invitations: MutableList<Invitation>? = null,
        val annotations: MutableList<CalendarTimelineAnnotation>? = null
)

data class CalendarTimelineAnnotation(
        val display: Boolean,
        val annotation: String,
        val htmlClass: String
)