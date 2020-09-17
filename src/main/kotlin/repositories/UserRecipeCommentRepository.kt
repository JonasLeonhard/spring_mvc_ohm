package repositories

import models.UserRecipeComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRecipeCommentRepository : JpaRepository<UserRecipeComment, Long> {

    @Query("FROM UserRecipeComment comment WHERE comment.recipe.id = :recipeId")
    fun findAllCommentsForRecipe(@Param("recipeId") recipeId: Long): MutableList<UserRecipeComment>
}