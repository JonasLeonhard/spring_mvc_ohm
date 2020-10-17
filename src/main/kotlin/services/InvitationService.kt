package services

import models.Invitation
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

    fun createInvitation(user: User, invitationForm: InvitationForm): Invitation {

        val recipe = if (invitationForm.recipeId != null) {
            recipeService.getRecipeById(invitationForm.recipeId).get()
        } else {
            throw Exception("{createInvitation()}: Recipe Id Exception, invitationForm.recipeId is null. This should have been validated at this point")
        }


        val friends = if (invitationForm.friends != null) {
            userService.findByUsernames(invitationForm.friends)
        } else {
            mutableListOf()
        }

        val date = if (invitationForm.date != null) {
            SimpleDateFormat("yyyy-MM-dd").parse(invitationForm.date)
        } else {
            throw Exception("{createInvitation()}: Date parsing Exception: Date has to be validated at this point")
        }
        println("create Invitation: invitationForm.date: ${invitationForm.date}, parsedDate: $date")

        val invitation = Invitation(
                recipe = recipe,
                user = user,
                friends = friends,
                date = date,
                message = invitationForm.message ?: ""
        )
        println("CREATE INVITATION: $invitation")
        return invitationRepository.save(invitation)
    }

    fun getInvitationById(invitationId: Long): Optional<Invitation> {
        return invitationRepository.findById(invitationId)
    }

    fun getInvitationComments(invitationId: Long): MutableList<UserInvitationComment> {
        return invitationCommentRepository.findAllCommentsForInvitation(invitationId)
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