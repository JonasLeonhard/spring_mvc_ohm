package controllers

import models.Profile
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import repositories.ProfileRepository


@Controller
class IndexController(private val profileRepository: ProfileRepository) {
    @GetMapping("/")
    fun index(model: Model): String {
        val user = Profile(1, "test")
        profileRepository.save(user)
        println("a new user was saved!")

        model["data"] = "ITWORKS:::"
        model["pageTitle"] = "TEST"
        return "index"
    }
}