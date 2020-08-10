package controllers

import models.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
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
            println("userprofile ::: $profile")

            if (principal != null) {
                val authenticated = userService.findByUsername(principal.name)
                model["authenticated"] = authenticated

                if (authenticated != profile) {
                    val friendship = userService.findFriendshipBetween(authenticated, profile)
                    println(profile.friendshipsFromThisUser.size)
                    println("------------found friendship: ${friendship?.id} ....")
                    if (friendship != null) model["friendship"] = friendship
                }
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

    @PostMapping("/friendship")
    fun friendship(principal: Principal, @RequestParam(value = "userId", required = true) userId: Long, model: Model): String {
        println("post to firendship ... create friendship between $principal and form id user with id $userId...")
        val authenticated = userService.findByUsername(principal.name)
        val toUser = userService.findById(userId)

        model["authenticated"] = authenticated
        model["pageTitle"] = "${principal.name}' Settings"
        model["friendship"] = userService.friendRequest(authenticated, toUser)
        return "profile"
    }
}