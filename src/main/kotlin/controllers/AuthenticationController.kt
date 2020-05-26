package controllers

import models.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import services.SecurityService
import services.UserService
import validators.UserValidator
import javax.validation.Valid

@Controller
class AuthenticationController(val securityService: SecurityService, val userService: UserService, val userValidator: UserValidator) {

    @GetMapping("/login")
    fun login(model: Model): String {
        model["user"] = User()
        model["pageTitle"] = "Login"
        return "login"
    }

    @GetMapping("/registration")
    fun registration(model: Model): String {
        model["user"] = User()
        model["pageTitle"] = "Registration"

        println("--- called /registration: send registration page ---")
        return "registration"
    }

    @PostMapping("/registration")
    fun registration(@Valid @ModelAttribute userForm: User, bindingResult: BindingResult, model: Model): String {
        userValidator.validate(userForm, bindingResult)
        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
            return "registration"
        }
        try {
            userService.registerNewUser(userForm)
        } catch (err: Error) {
            return "redirect:/registration?error=true"
        }
        println("--- REDIRECT POST, got $userForm : /registration to / ... autoLogin ...---")
        securityService.autoLogin(userForm.username, userForm.password)
        return "redirect:/login?hasRegistered=true&username=${userForm.username}"
    }
}