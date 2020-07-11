package repositories

import models.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IngredientRepository : JpaRepository<Ingredient, Long> {

    @Query("SELECT ingredient FROM Ingredient ingredient WHERE ingredient.name = ?1")
    fun findIngredientByName(name: String): Ingredient?
}