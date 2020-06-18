package controllers

import configurations.ApplicationPropertiesConfiguration
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import services.UserService
import java.security.Principal


@Controller
@RequestMapping("/")
class IndexController(val userService: UserService, val prop: ApplicationPropertiesConfiguration) {
    @GetMapping
    fun index(model: Model, principal: Principal?): String {
        model["pageTitle"] = "INDEXPAGETITLE"
        if (principal != null) {
            model["authenticated"] = userService.findByUsername(principal.name)
        }
        return "index"
    }
}