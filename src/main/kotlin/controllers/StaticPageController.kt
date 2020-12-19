package controllers

import models.ContactUs
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import repositories.ContactUsRepository
import services.UserService
import java.security.Principal
import javax.validation.Valid

@Controller
class StaticPageController(val userService: UserService, val contactUsRepository: ContactUsRepository) {
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
        model["contactUs"] = ContactUs()
        userService.addAuthenticatedUserToModel(principal, model)
        return "contact"
    }

    @PostMapping("/contact")
    fun createContactUs(principal: Principal?, @Valid contactUs: ContactUs, bindingResult: BindingResult, model: Model): String {
        model["pageTitle"] = "Contact"
        userService.addAuthenticatedUserToModel(principal, model)
        model["contactUs"] = contactUs

        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
        } else {
            contactUsRepository.save(contactUs)
            model["success"] = true
        }
        return "contact"
    }
}