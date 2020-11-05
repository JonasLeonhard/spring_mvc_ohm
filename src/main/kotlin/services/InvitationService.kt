package services

import models.Invitation
import models.Recipe
import models.User
import models.UserInvitationComment
import org.springframework.stereotype.Service
import pojos.InvitationForm
import repositories.InvitationRepository
import repositories.UserInvitationCommentRepository
import java.text.SimpleDateFormat
import java.util.*

@Service
class InvitationService(val recipeService: RecipeService,
                        val userService: UserService,
                        val invitationRepository: InvitationRepository,
                        val invitationCommentRepository: UserInvitationCommentRepository) {

    fun deleteInvitation(user: User, invitationForm: InvitationForm, invitationId: Long) {
        val invitation = invitationRepository.findById(invitationId).get()
        if (invitation.user.id != user.id) {
            throw Exception("Delete Invitation Exception at (deleteInvitation): User has no rights to delete invitation")
        }
        invitationCommentRepository.deleteAll(getInvitationComments(invitationId))
        invitationRepository.delete(invitation)
    }

    fun editInvitation(user: User, invitationForm: InvitationForm, invitationId: Long): Invitation {
        val invitation = invitationRepository.findById(invitationId).get()
        if (invitation.user.id != user.id) {
            throw Exception("Edit Invitation Exception at (editInvitation): User has no rights to edit invitation!")
        }
        val recipe = invitationFormGetRecipe(invitationForm)
        val friends = invitationFormGetFriends(invitationForm)
        val date = invitationFormGetDate(invitationForm)
        invitation.recipe = recipe
        invitation.friends = friends
        invitation.date = date
        invitation.message = invitationForm.message ?: ""
        return invitationRepository.save(invitation)
    }

    fun createInvitation(user: User, invitationForm: InvitationForm): Invitation {
        val recipe = invitationFormGetRecipe(invitationForm)
        val friends = invitationFormGetFriends(invitationForm)
        val date = invitationFormGetDate(invitationForm)
        val invitation = Invitation(
                recipe = recipe,
                user = user,
                friends = friends,
                date = date,
                message = invitationForm.message ?: ""
        )
        return invitationRepository.save(invitation)
    }

    fun getInvitationById(invitationId: Long): Optional<Invitation> {
        return invitationRepository.findById(invitationId)
    }

    fun getInvitationComments(invitationId: Long): MutableList<UserInvitationComment> {
        return invitationCommentRepository.findAllCommentsForInvitation(invitationId)
    }

    @Throws(Exception::class)
    fun invitationFormGetRecipe(invitationForm: InvitationForm): Recipe {
        return if (invitationForm.recipeId != null) {
            recipeService.getRecipeById(invitationForm.recipeId).get()
        } else {
            throw Exception("{createInvitation()}: Recipe Id Exception, invitationForm.recipeId is null. This should have been validated at this point")
        }
    }

    fun invitationFormGetFriends(invitationForm: InvitationForm): MutableList<User> {
        return if (invitationForm.friends != null) {
            userService.findByUsernames(invitationForm.friends)
        } else {
            mutableListOf()
        }
    }

    @Throws(Exception::class)
    fun invitationFormGetDate(invitationForm: InvitationForm): Date {
        return if (invitationForm.date != null) {
            SimpleDateFormat("yyyy-MM-dd").parse(invitationForm.date)
        } else {
            throw Exception("{createInvitation()}: Date parsing Exception: Date has to be validated at this point")
        }
    }

    /**
     * Gets the invitationForm for a given invitationId.
     * Throws Exception if the user is not the user that created the Invitation
     * */
    fun getUserInvitationFormByInvitationId(user: User, invitationId: Long): InvitationForm {
        val invitation = getInvitationById(invitationId).get()
        if (invitation.user.id != user.id) {
            throw Exception("403: getUserInvitationFormByInvitationId(), User has not Created the Invitation")
        }
        val invitationFriends = invitation.friends.map { friend -> friend.username }.toMutableList()
        println("GetInvittaionFromByInvitationId() -> toString: ${invitation.date}")
        return InvitationForm(
                invitation.recipe.id, invitation.message, invitationFriends, invitation.date.toString())
    }
}