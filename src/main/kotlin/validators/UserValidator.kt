package validators

import models.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
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
        fileMimeTypeValid(errors, user)
    }

    override fun supports(aClass: Class<*>): Boolean {
        return User::class.java == aClass
    }

    fun usernameExists(errors: Errors, user: User) {
        try {
            userService.findByUsername(user.username)
            errors.rejectValue("username", "UsernameExists", "this Username already exists")
        } catch (esc: UsernameNotFoundException) {
        }
    }

    fun passwordNotMatching(errors: Errors, user: User) {
        println("validate password: ${user.passwordConfirm} -> ${user.password} ----------")
        if (user.passwordConfirm != user.password) {
            errors.rejectValue("password", "PasswordNotMatching", "password is not matching the confirm password")
        }
    }

    fun fileMimeTypeValid(errors: Errors, user: User) {
        val file = user.file
        if (
                file != null &&
                !file.isEmpty &&
                !(file.contentType?.toLowerCase().equals("image/jpg")
                        || file.contentType?.toLowerCase().equals("image/jpeg")
                        || file.contentType?.toLowerCase().equals("image/png"))) {
            errors.rejectValue("file", "InvalidMimeTypeException", "jpg, jpeg & png file types are only supported")
        }
    }
}