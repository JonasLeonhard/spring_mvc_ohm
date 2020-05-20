package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import repositories.UserRepository


@Controller
@RequestMapping("/")
class IndexController(private val userRepository: UserRepository) {

    @GetMapping
    fun index(model: Model): String {
        //val user = User(1, "test")
        //userRepository.save(user)
        println("a new user was saved!")

        model["data"] = "ITWORKS:::"
        model["pageTitle"] = "TEST"
        return "index"
    }
}