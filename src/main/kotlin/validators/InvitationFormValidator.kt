package validators

import models.User
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import pojos.InvitationForm
import services.RecipeService
import services.UserService
import java.security.Principal
import java.text.SimpleDateFormat

@Component
class InvitationFormValidator(val userService: UserService, val recipeService: RecipeService) {
    fun validate(principal: Principal, target: InvitationForm, errors: Errors) {
        val user = userService.findByUsername(principal.name)

        friendshipValidation(user, target, errors)
        recipeValidation(target, errors)
        dateValidation(target, errors)
    }

    fun friendshipValidation(user: User, target: InvitationForm, errors: Errors) {
        target.friends?.forEach { username ->
            val friend = userService.findByUsername(username)
            val friendship = userService.findFriendshipBetween(user, friend)
            if (friendship == null || !friendship.accepted) {
                errors.rejectValue("friends", "FriendsException", "Please Try again. Friendship could not be found")
            }
        }
    }

    fun recipeValidation(target: InvitationForm, errors: Errors) {
        if (target.recipeId != null) {
            val recipeOptional = recipeService.getRecipeById(target.recipeId)
            if (!recipeOptional.isPresent) {
                errors.rejectValue("recipeId", "RecipePresentException", "Please Try again. Recipe could not be found")
            }
        } else {
            errors.rejectValue("recipeId", "RecipeIdException", "Please Try again. Recipe id is missing")
        }
    }

    fun dateValidation(target: InvitationForm, errors: Errors) {
        try {
            println("dateValidation of: ${target.date}")
            SimpleDateFormat("yyyy-MM-dd").parse(target.date)
        } catch (exception: Exception) {
            println("exception: $exception")
            errors.rejectValue("date", "DateException", "Date format is wrong: Try dd/MM/yyyy")
        }
    }
}