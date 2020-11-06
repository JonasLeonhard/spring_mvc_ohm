package services

import org.springframework.stereotype.Service
import java.time.LocalDate


@Service
class CalendarService {

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