package repositories

import models.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : JpaRepository<Recipe, Long> {

    /**
     * Returns a Recipe by its api id
     * @param recipeSummaryId is equal to its recipe.spoonacularId
     */
    @Query("SELECT recipe FROM Recipe recipe WHERE recipe.spoonacularId = :recipeSummary")
    fun getIndexedRecipeByApiId(@Param("recipeSummary") recipeSummaryId: Long): Recipe?

    @Query("SELECT recipe FROM Recipe recipe WHERE lower(recipe.summary) LIKE lower(concat('%', ?1, '%'))")
    fun searchForRecipe(q: String): MutableList<Recipe>
}