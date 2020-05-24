package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import services.UserDetailsService
import java.security.Principal

@Controller
@RequestMapping("/user")
class UserController(val userDetailsService: UserDetailsService) {

    @GetMapping("/profile/{username}")
    fun userProfile(principal: Principal?, model: Model, @PathVariable username: String): String {
        if (principal != null) {
            model["authenticated"] = userDetailsService.loadUserByUsername(principal.name)
        }

        try {
            model["profile"] = userDetailsService.loadUserByUsername(username)
        } catch (userNotFound: Exception) {
            model["userNotFound"] = username
        }

        model["pageTitle"] = "${username}'s Profile"
        return "profile"
    }

    @GetMapping("/settings")
    fun userSettings(principal: Principal, model: Model): String {
        model["authenticated"] = userDetailsService.loadUserByUsername(principal.name)
        model["pageTitle"] = "${principal.name}' Settings"
        return "settings"
    }
}