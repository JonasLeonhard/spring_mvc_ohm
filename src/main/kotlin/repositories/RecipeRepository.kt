package repositories

import models.Recipe
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pojos.MostLikedRecipeProjection

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

    @Query("""
        SELECT NEW pojos.MostLikedRecipeProjection(recipe, COUNT(userLikes.embeddedKey.recipe.id)) 
        FROM Recipe recipe 
        JOIN recipe.userLikes userLikes 
        GROUP BY recipe 
        ORDER BY COUNT(userLikes.embeddedKey.recipe.id) ASC
    """)
    fun getMostLikedRecipes(pageable: Pageable): MutableList<MostLikedRecipeProjection>

    @Query("SELECT recipe FROM Recipe recipe ORDER BY recipe.createdAt ASC")
    fun getNewestRecipes(pageable: Pageable): MutableList<Recipe>

    @Query("SELECT recipe.id FROM Recipe recipe")
    fun getRecipeIds(): MutableList<Long>
}