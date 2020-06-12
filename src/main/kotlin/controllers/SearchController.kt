package controllers

import org.springframework.http.HttpHeaders
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
    fun searchResults(model: Model, @RequestParam(value = "q") q: String, @RequestParam(value = "type") type: String): String {
        model["q"] = q
        model["type"] = type
        model["pageTitle"] = "Search '$q'"

        setSearchUsers(model, type, q)
        setSearchRecipes(model, type)
        return "search"
    }

    fun setSearchUsers(model: Model, type: String, q: String) {
        if (type == "everywhere" || type == "users") {
            val searchUsers = userService.searchAllByUsername(q)
            if (searchUsers.count() > 0) {
                model["searchUsers"] = searchUsers
            }
        }
    }

    fun setSearchRecipes(model: Model, type: String) {
        // creat restTemplate Bean
        // check if already searched for by spoonacular api
        // if not: search in api and add to database
        val httpHeader = HttpHeaders()

        if (type == "everywhere" || type == "recipes") {
            // TODO
        }
    }
}

