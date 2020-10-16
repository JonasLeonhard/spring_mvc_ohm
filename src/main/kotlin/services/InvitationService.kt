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
        val recipe = recipeService.getRecipeById(invitationForm.recipeId).get()


        val friends = if (invitationForm.friends != null) {
            userService.findByUsernames(invitationForm.friends)
        } else {
            mutableListOf()
        }
//        // Gets all friendships for the given user and filters all accepted friendships contained in invitationForm.friends usernames
//        val userFriendships = if (invitationForm.friends != null) {
//            userService.getFriendships(user).filter { friendship ->
//                if (friendship.requested_by.id == user.id) {
//                    friendship.accepted && invitationForm.friends.contains(friendship.request_to.username)
//                } else {
//                    friendship.accepted && invitationForm.friends.contains(friendship.requested_by.username)
//                }
//            }
//        } else {
//            mutableListOf()
//        }.toMutableList()


        val date = if (invitationForm.date != null) {
            SimpleDateFormat("dd-MM-yyyy").parse(invitationForm.date)
        } else {
            throw Exception("{createInvitation()}: Date parsing Exception: Date has to be validated at this point")
        }

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
}