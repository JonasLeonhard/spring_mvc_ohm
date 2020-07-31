package configurations

import models.User
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException
import org.springframework.core.env.Environment
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import services.UserService
import java.security.Principal

@ControllerAdvice
class ExceptionHandlerConfiguration(val environment: Environment,
                                    val props: ApplicationPropertiesConfiguration,
                                    val userService: UserService) {

    @ExceptionHandler(value = [Exception::class])
    fun globalErrorHandling(principal: Principal?, exception: Exception, model: Model): String {
        println("ExceptionController --- ${props.ANSI_RED} ${exception::class}: ${exception.message ?: "Undefined exception message"} ${props.ANSI_RESET}")
        userService.addAuthenticatedUserToModel(principal, model)
        model["errorMsg"] = "Oops, an error occurred while handling your request, please try again."
        return "error"
    }

    @ExceptionHandler(value = [MaxUploadSizeExceededException::class, SizeLimitExceededException::class])
    fun handleSizeExceededException(principal: Principal?, exception: Exception, model: Model): String {
        userService.addAuthenticatedUserToModel(principal, model)
        model["user"] = User()
        model["fileUploadException"] = "max filesize cannot exceed ${environment.getProperty("spring.servlet.multipart.max-file-size")}"
        model["fileLimitExceedException"] = "max request size is ${environment.getProperty("spring.servlet.multipart.max-request-size")}"
        return "registration"
    }

}