package controllers

import models.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import services.SecurityService
import services.UserService

@Controller
class AuthenticationController(val securityService: SecurityService, val userService: UserService) {

    @GetMapping("/login")
    fun login(model: Model): String {
        model["user"] = User()
        model["pageTitle"] = "registration"

        println("--- /login: return login page ---")
        return "login"
    }

    @GetMapping("/registration")
    fun registration(model: Model): String {
        model["user"] = User()
        model["pageTitle"] = "registration"

        println("--- called /registration: send registration page ---")
        return "registration"
    }

    @PostMapping("/registration")
    fun registration(@ModelAttribute("userForm") userForm: User): String {
        // TODO()
        // check for validity
        // check if already exists
        try {
            userService.save(userForm)
        } catch (err: Error) {
            return "redirect:/registration?error=true"
        }
        println("--- REDIRECT POST, got $userForm : /registration to / ---")
        securityService.autoLogin(userForm.username, userForm.password)
        return "redirect:/login?hasRegistered=true&username=${userForm.username}"
    }
}