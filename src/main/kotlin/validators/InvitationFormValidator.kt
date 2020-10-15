package validators

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import pojos.InvitationForm
import services.UserService
import java.security.Principal

@Component
class InvitationFormValidator(val userService: UserService) {
    fun validate(principal: Principal, target: InvitationForm, errors: Errors) {
        val user = userService.findByUsername(principal.name)

        target.friends?.forEach { username ->
            val friend = userService.findByUsername(username)
            val friendship = userService.findFriendshipBetween(user, friend)
            if (friendship == null || !friendship.accepted) {
                errors.rejectValue("friends", "FriendsException", "Please Try again. Friendship could not be found")
            }
        }
    }
}