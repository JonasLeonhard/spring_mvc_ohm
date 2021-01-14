package repositories

import models.UserInvitationItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserInvitationItemRepository : JpaRepository<UserInvitationItem, Long> {

    @Query("SELECT invitationItem FROM UserInvitationItem invitationItem WHERE invitationItem.invitation.id = :invitationId")
    fun findInvitationItemByInvitationId(@Param("invitationId") invitationId: Long): MutableList<UserInvitationItem>

    @Query("SELECT invitationItem FROM UserInvitationItem invitationItem WHERE invitationItem.recipeIngredient.embeddedKey.recipe.id = :recipeId AND invitationItem.recipeIngredient.embeddedKey.ingredient.id = :ingredientId AND invitationItem.user.id = :userId")
    fun findInvitationItem(@Param("userId") userId: Long, @Param("recipeId") recipeId: Long, @Param("ingredientId") ingredientId: Long): UserInvitationItem?
}