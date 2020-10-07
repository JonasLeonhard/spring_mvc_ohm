package repositories

import models.Freezer
import models.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pojos.RecipeSuggestions
import java.util.*

@Repository
interface FreezerRepository : JpaRepository<Freezer, Long> {

    @Query("SELECT freezer FROM Freezer freezer WHERE freezer.embeddedKey.user.id = :userId AND freezer.embeddedKey.ingredient.id = :ingredientId")
    fun findById(@Param("userId") userId: Long, @Param("ingredientId") ingredientId: Long): Optional<Freezer>

    @Query("SELECT freezer FROM Freezer freezer WHERE freezer.embeddedKey.user.id = :userId ORDER BY freezer.amount, freezer.embeddedKey.ingredient.name ASC")
    fun findByUserId(@Param("userId") userId: Long): Optional<MutableList<Freezer>>

    @Query("""SELECT NEW pojos.RecipeSuggestions(recipe, COUNT(recipeIngredients.embeddedKey.recipe.id)) from Recipe recipe 
                    JOIN recipe.recipeIngredients recipeIngredients 
                    JOIN recipeIngredients.embeddedKey.ingredient ingredient 
                    WHERE ingredient IN(:ingredients)
                    GROUP BY recipe
                    ORDER BY COUNT(recipeIngredients.embeddedKey.recipe.id) DESC
                    """)
    fun findSuggestions(@Param("ingredients") ingredients: MutableList<Ingredient>): MutableList<RecipeSuggestions>
}