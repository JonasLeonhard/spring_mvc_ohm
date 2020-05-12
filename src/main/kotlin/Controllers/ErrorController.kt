package Controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController as DefaultErrorController


@Controller
class ErrorController : DefaultErrorController {
    val path = "/error"

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest, model: Model): String {
        val statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
        model["statusCode"] = statusCode

        when (statusCode) {
            404 -> {
                model["errorMsg"] = "Our apologies, this page went out to visit the family."
            }
            else -> {
                model["errorMsg"] = "Oops, something went wrong. Please try again"
            }
        }
        return "error"
    }

    override fun getErrorPath(): String {
        return path
    }
}