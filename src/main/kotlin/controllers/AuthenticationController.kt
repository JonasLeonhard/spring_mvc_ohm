package controllers

import models.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import repositories.UserRepository
import services.SecurityService

@Controller
class AuthenticationController(val securityService: SecurityService, val userRepository: UserRepository) {

    @GetMapping("/login")
    fun login(): String {
        // TODO()
//        val roles: Set<Role> = setOf<Role>(Role(1, "test", setOf()))
//        val user = User(1, "test", "123", "123", "defaultName")
//        userRepository.save(user)
        println("--- /login: return login page ---")
        return "login"
    }

    @GetMapping("/registration")
    fun registration(model: Model): String {
        model["user"] = "User() here!"
        // TODO()
        println("--- called /registration: send registration page ---")
        model["pageTitle"] = "registration"
        return "registration"
    }

    @PostMapping("/registration")
    fun registration(@ModelAttribute("userForm") userForm: User): String {
        // TODO()
        // check for validity
        // check if already exists
        userRepository.save(userForm)
        println("--- REDIRECT POST, got $userForm : /registration to / ---")
        securityService.autoLogin("test", "test");
        return "redirect:/"
    }
}