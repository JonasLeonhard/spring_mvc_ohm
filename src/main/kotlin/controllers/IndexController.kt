package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import services.UserDetailsService
import java.security.Principal


@Controller
@RequestMapping("/")
class IndexController(val userDetailsService: UserDetailsService) {

    @GetMapping
    fun index(model: Model, principal: Principal): String {
        model["pageTitle"] = "INDEXPAGETITLE"
        model["user"] = userDetailsService.loadUserByUsername(principal.name)
        return "index"
    }
}