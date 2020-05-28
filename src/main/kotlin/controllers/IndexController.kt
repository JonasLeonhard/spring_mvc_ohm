package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import services.SecurityService
import java.security.Principal


@Controller
@RequestMapping("/")
class IndexController(val securityService: SecurityService) {

    @GetMapping
    fun index(model: Model, principal: Principal): String {
        model["pageTitle"] = "INDEXPAGETITLE"
        model["authenticated"] = securityService.getAuthenticatedUser(principal)
        println("securityService.getAuthenticatedUser() ${securityService.getAuthenticatedUser(principal)}")
        return "index"
    }
}