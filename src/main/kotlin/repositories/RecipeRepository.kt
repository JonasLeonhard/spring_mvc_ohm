package repositories

import models.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : JpaRepository<Recipe, Long> {

    @Query("SELECT recipe FROM Recipe recipe WHERE recipe.spoonacularId = :#{#recipeSummaryId}")
    fun getIndexedRecipe(@Param("recipeSummary") recipeSummaryId: Long): Recipe?

    @Query("SELECT recipe FROM Recipe recipe WHERE lower(recipe.summary) LIKE lower(concat('%', ?1, '%'))")
    fun searchForRecipe(q: String): MutableList<Recipe>
}