package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import services.UserService

@Controller
@RequestMapping("/search")
class SearchController(val userService: UserService) {

    @GetMapping("")
    fun searchBar(model: Model, @RequestParam(value = "q") q: String, @RequestParam(value = "type") type: String): String {
        model["q"] = q
        model["type"] = type

        if (type == "everywhere" || type == "users") {
            val searchUsers = userService.searchAllByUsername(q)
            if (searchUsers.count() > 0) {
                model["searchUsers"] = searchUsers
            }
        }

        if (type == "everywhere" || type == "recipes") {
            // TODO
        }
        return "search"
    }
}