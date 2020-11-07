package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import services.CalendarService
import services.UserService
import java.security.Principal
import java.time.LocalDate

@Controller
@RequestMapping("/calendar")
class CalendarController(val userService: UserService,
                         val calendarService: CalendarService) {

    @GetMapping
    fun calendar(principal: Principal, model: Model, @RequestParam(name = "selectedDate") selectedDateStr: String?): String {
        val user = userService.findByUsername(principal.name)
        model["authenticated"] = user

        var selectionDate = LocalDate.now()
        if (selectedDateStr != null) {
            selectionDate = LocalDate.parse(selectedDateStr)
        }

        model["calendarDates"] = calendarService.getCalendarFor(selectionDate)
        model["calendarTimelines"] = calendarService.getTimeLineFor(user, selectionDate)
        model["selectedDate"] = selectionDate
        model["todayDate"] = LocalDate.now()

        return "calendar"
    }

}