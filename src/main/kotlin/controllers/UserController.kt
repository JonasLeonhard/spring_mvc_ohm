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

            if (principal != null) {
                val authenticated = userService.findByUsername(principal.name)
                model["authenticated"] = authenticated

                if (authenticated != profile) {
                    val friendship = userService.findFriendshipBetween(authenticated, profile)
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
        userService.addAuthenticatedUserToModel(principal, model)
        model["pageTitle"] = "${principal.name}' Settings"
        return "settings"
    }

    @GetMapping("/buyList")
    fun userBuyList(principal: Principal, model: Model): String {
        userService.addAuthenticatedUserToModel(principal, model)
        return "buyList"
    }

    @GetMapping("/freezer")
    fun freezer(principal: Principal, model: Model): String {
        userService.addAuthenticatedUserToModel(principal, model)
        return "freezer"
    }

    @PostMapping("/friendship")
    fun friendship(principal: Principal, @RequestParam(value = "userId", required = true) userId: Long, model: Model): String {
        val authenticated = userService.findByUsername(principal.name)
        val toUser = userService.findById(userId)

        model["authenticated"] = authenticated
        model["pageTitle"] = "${principal.name}' Settings"
        model["friendship"] = userService.friendRequest(authenticated, toUser)
        return "redirect:/user/profile/${toUser.username}"
    }

    @PostMapping("/friendship/accept")
    fun friendshipAccept(principal: Principal, @RequestParam(value = "userId", required = true) userId: Long, model: Model): String {
        val authenticated = userService.findByUsername(principal.name)
        val toUser = userService.findById(userId)
        val accept = userService.friendRequestAccept(authenticated, toUser)
        println("friendrequest accept: $accept")
        return "redirect:/user/profile/${toUser.username}"
    }

    @PostMapping("/friendship/cancel")
    fun friendshipCancel(principal: Principal, @RequestParam(value = "userId", required = true) userId: Long, model: Model): String {
        val authenticated = userService.findByUsername(principal.name)
        val toUser = userService.findById(userId)
        val test = userService.friendRequestCancel(authenticated, toUser)
        println("friendrequest cancel::: $test")
        return "redirect:/user/profile/${toUser.username}"
    }
}