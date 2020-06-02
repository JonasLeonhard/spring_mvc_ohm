package controllers

import models.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import services.UserService
import java.security.Principal

@Controller
@RequestMapping("/user")
class UserController(val userService: UserService) {

    @GetMapping("/profile/{username}")
    fun userProfile(principal: Principal?, model: Model, @PathVariable username: String): String {
        model["userForm"] = User()
        try {
            val profile = userService.findByUsername(username)
            model["profile"] = profile

            if (principal != null) {
                val authenticated = userService.findByUsername(principal.name)
                model["authenticated"] = authenticated

                val friendship = userService.findFriendshipBetween(authenticated, profile)
                if (friendship != null) model["friendship"] = friendship
            }

        } catch (userNotFound: Exception) {
            model["userNotFound"] = username
        }

        model["pageTitle"] = "${username}'s Profile"
        return "profile"
    }

    @GetMapping("/settings")
    fun userSettings(principal: Principal, model: Model): String {
        model["authenticated"] = userService.findByUsername(principal.name)
        model["pageTitle"] = "${principal.name}' Settings"
        return "settings"
    }
}