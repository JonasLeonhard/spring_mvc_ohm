package repositories

import models.RecipeIngredients
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RecipeIngredientsRepository : JpaRepository<RecipeIngredients, Long> {
    @Query("FROM RecipeIngredients recipeIngredients WHERE recipeIngredients.embeddedKey.recipe.id = :recipeId AND recipeIngredients.embeddedKey.ingredient.id IN(:ingredientIds)")
    fun findAllByEmbedded(@Param("recipeId") recipeId: Long, @Param("ingredientIds") ingredientIds: List<Long>): MutableList<RecipeIngredients>
}