package validators

import models.User
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import services.UserService

@Component
class UserValidator(val userService: UserService) : Validator {
    override fun validate(target: Any, errors: Errors) {
        val user = target as User
        usernameExists(errors, user)
        passwordNotMatching(errors, user)
    }

    override fun supports(aClass: Class<*>): Boolean {
        return User::class.java == aClass
    }

    fun usernameExists(errors: Errors, user: User) {
        println("validate username: ${user.username} ----------")
        if (userService.findByUserName(user.username) != null) {
            errors.rejectValue("username", "UsernameExists", "this Username already exists")
        }
    }

    fun passwordNotMatching(errors: Errors, user: User) {
        println("validate password: ${user.passwordConfirm} -> ${user.password} ----------")
        if (user.passwordConfirm != user.password) {
            errors.rejectValue("password", "PasswordNotMatching", "password is not matching the confirm password")
        }
    }
}