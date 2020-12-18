package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import services.UserService
import java.security.Principal

@Controller
class StaticPageController(val userService: UserService) {
    @GetMapping("/about")
    fun about(principal: Principal?, model: Model): String {
        model["pageTitle"] = "About"
        userService.addAuthenticatedUserToModel(principal, model)
        return "about"
    }

    @GetMapping("/terms")
    fun terms(principal: Principal?, model: Model): String {
        model["pageTitle"] = "Terms"
        userService.addAuthenticatedUserToModel(principal, model)
        return "terms"
    }

    @GetMapping("/privacy")
    fun privacy(principal: Principal?, model: Model): String {
        model["pageTitle"] = "Privacy"
        userService.addAuthenticatedUserToModel(principal, model)
        return "privacy"
    }

    @GetMapping("/contact")
    fun contact(principal: Principal?, model: Model): String {
        model["pageTitle"] = "Contact"
        userService.addAuthenticatedUserToModel(principal, model)
        return "contact"
    }
}