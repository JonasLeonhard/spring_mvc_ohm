package services

import models.User
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class SecurityService(val userService: UserService) {

    fun getAuthenticatedUser(principal: Principal): User {
        return userService.findByUsername(principal.name)
                ?: throw SecurityServiceException("User could not be found from principal")
    }
}

class SecurityServiceException(message: String) : Exception(message) {}

