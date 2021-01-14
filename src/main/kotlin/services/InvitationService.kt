package services

import models.*
import org.springframework.stereotype.Service
import pojos.InvitationForm
import repositories.InvitationRepository
import repositories.RecipeIngredientsRepository
import repositories.UserInvitationCommentRepository
import repositories.UserInvitationItemRepository
import java.security.Principal
import java.text.SimpleDateFormat
import java.util.*
import javax.transaction.Transactional

@Service
class InvitationService(val recipeService: RecipeService,
                        val userService: UserService,
                        val invitationRepository: InvitationRepository,
                        val recipeIngredientsRepository: RecipeIngredientsRepository,
                        val userInvitationItemRepository: UserInvitationItemRepository,
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
        invitation.title = invitationForm.title ?: ""
        invitation.message = invitationForm.message ?: ""
        invitation.gridRowStart = invitationForm.gridRowStart ?: 0
        invitation.gridRowEnd = invitationForm.gridRowEnd ?: 0
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
                title = invitationForm.title ?: "",
                message = invitationForm.message ?: "",
                gridRowStart = invitationForm.gridRowStart ?: 0,
                gridRowEnd = invitationForm.gridRowEnd ?: 0
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
                invitation.recipe.id,
                invitation.title,
                invitation.message,
                invitation.gridRowStart,
                invitation.gridRowEnd,
                invitationFriends,
                invitation.date.toString())
    }

    fun getUserInvitationItemsForInvitation(invitation: Invitation): MutableList<UserInvitationItem> {
        return userInvitationItemRepository.findInvitationItemByInvitationId(invitation.id)
    }

    @Transactional
    fun saveInvitationItem(invitationId: Long, principal: Principal, recipeId: Long?, ingredientId: Long?, otherItem: String?): UserInvitationItem {
        val user = userService.findByUsername(principal.name)
        val invitation = getInvitationById(invitationId).get()
        println("recipeId: $recipeId, ingredientId: $ingredientId, otherItem: $otherItem")
        if (ingredientId != null && recipeId != null) {
            val recipeIngredient = recipeIngredientsRepository.findByEmbedded(recipeId, ingredientId).get()
            val userInvitationItem = UserInvitationItem(invitation = invitation, user = user, recipeIngredient = recipeIngredient)
            userInvitationItemRepository.deleteOtherUserInvitationItems(user.id, recipeId, ingredientId)
            return userInvitationItemRepository.save(userInvitationItem)
        } else if (otherItem != null) {
            val userInvitationItem = UserInvitationItem(invitation = invitation, user = user, item = otherItem)
            return userInvitationItemRepository.save(userInvitationItem)
        }

        throw Exception("Error: neither recipeIngredient nor otherItem given.")
    }

    fun getUserByRecipeIngredient(recipeIngredient: RecipeIngredients, invitationItems: MutableList<UserInvitationItem>): User? {
        val isRecipeItem = invitationItems.filter { it.recipeIngredient?.recipe?.id == recipeIngredient.recipe.id && it.recipeIngredient.ingredient.id == recipeIngredient.ingredient.id }

        if (isRecipeItem.size == 1) {
            return isRecipeItem[0].user
        }
        return null
    }
}