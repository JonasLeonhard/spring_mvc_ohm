package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import services.SuggestionService
import services.UserService
import java.security.Principal


@Controller
@RequestMapping("/")
class IndexController(val userService: UserService, val suggestionService: SuggestionService) {
    @GetMapping
    fun index(model: Model, principal: Principal?): String {
        model["pageTitle"] = "INDEXPAGETITLE"
        model["suggestions"] = suggestionService.getIndexSuggestions()
        userService.addAuthenticatedUserToModel(principal, model)

        return "index"
    }
}