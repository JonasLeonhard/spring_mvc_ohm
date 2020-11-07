package services

import models.User
import org.springframework.stereotype.Service
import pojos.CalendarTimeline
import pojos.CalendarTimelineAnnotation
import repositories.InvitationRepository
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


@Service
class CalendarService(val invitationRepository: InvitationRepository) {

    /**
     * @return Gets a MutableList<LocalDate> for the selectionDate.
     * - Returns a List of all Days from the first Day of the selectionDate Month to the Last Day
     * of the selectionDate Month.
     * (This list is also filled with all dates to the next monday before the first day of the given month.
     * and all dates to the next sunday after the last day of the given month)
     * */
    fun getCalendarFor(selectionDate: LocalDate): MutableList<LocalDate> {
        val lastDayOfTheMonth = selectionDate.withDayOfMonth(selectionDate.month.length(selectionDate.isLeapYear))
        val firstDayOfTheMonth = selectionDate.withDayOfMonth(1)
        val selectedMonth = getDatesBetween(firstDayOfTheMonth, lastDayOfTheMonth)
        val dateList = mutableListOf<LocalDate>()

        if (firstDayOfTheMonth.dayOfWeek.value - 1 != 0) {
            val fillBeforeSelected = getDatesBetween(firstDayOfTheMonth.minusDays((firstDayOfTheMonth.dayOfWeek.value - 1).toLong()), firstDayOfTheMonth.minusDays(1))
            dateList.addAll(fillBeforeSelected)
        }

        dateList.addAll(selectedMonth)

        if (7 - lastDayOfTheMonth.dayOfWeek.value != 0) {
            val fillAfterSelected = getDatesBetween(lastDayOfTheMonth.plusDays(1), lastDayOfTheMonth.plusDays((7 - lastDayOfTheMonth.dayOfWeek.value).toLong()))
            dateList.addAll(fillAfterSelected)
        }

        return dateList
    }

    fun getTimeLineFor(user: User, selectionDate: LocalDate): MutableList<CalendarTimeline> {
        val timeLineWeekDays = getDatesBetween(
                selectionDate.minusDays(selectionDate.dayOfWeek.value.toLong() - 1),
                selectionDate.plusDays(7 - selectionDate.dayOfWeek.value.toLong()))

        val invitationsBetween = invitationRepository.findInvitationsBetween(
                user.id,
                getDateFromLocalDate(timeLineWeekDays[0]),
                getDateFromLocalDate(timeLineWeekDays[timeLineWeekDays.size - 1]))

        var minutes = 0
        var hours = 0
        val timelineAnnotations = mutableListOf(
                CalendarTimeline(
                        annotations = (0..96).toList().map map@{ timeStep ->
                            if (minutes == 60) {
                                minutes = 0
                                hours += 1
                            }

                            val annotation = CalendarTimelineAnnotation(
                                    display = (minutes == 0 || minutes == 60),
                                    annotation = "$hours:${if (minutes == 0 || minutes == 60) "00" else "$minutes"} ${if (timeStep <= 47) "AM" else "PM"}",
                                    htmlClass = "time-step-$timeStep")
                            minutes += 15

                            return@map annotation
                        }.toMutableList()
                )
        )

        val timelineEvents = timeLineWeekDays.map { localDate ->
            CalendarTimeline(
                    localDate = localDate,
                    invitations = invitationsBetween.filter { invitation ->
                        getLocalDateFromDate(invitation.date) == localDate
                    }.toMutableList())
        }.toMutableList()

        timelineAnnotations.addAll(timelineEvents)
        return timelineAnnotations
    }

    /**
     * @return Date at midnight from localDate conversion of the current system default timezone
     * */
    fun getDateFromLocalDate(localDate: LocalDate): Date {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    }

    fun getLocalDateFromDate(date: Date): LocalDate {
        return LocalDate.parse(date.toString())
    }

    /**
     * Gets a List of Dates Between start and end LocalDate's, !including! start and end LocalDate's
     * */
    fun getDatesBetween(start: LocalDate, end: LocalDate): MutableList<LocalDate> {
        var date = start
        val dates = mutableListOf<LocalDate>()
        while (date.isBefore(end)) {
            dates.add(date)
            date = date.plusDays(1)
        }
        dates.add(end)
        return dates
    }
}