package repositories

import models.UserRecipeBuyList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRecipeBuyListRepository : JpaRepository<UserRecipeBuyList, Long> {

   @Query("FROM UserRecipeBuyList userRecipeBuyList WHERE userRecipeBuyList.embeddedKey.recipe.id = :recipeId AND userRecipeBuyList.embeddedKey.user.id = :userId")
   fun findByEmbedded(@Param("userId") userId: Long, @Param("recipeId") recipeId: Long): UserRecipeBuyList
}