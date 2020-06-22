package repositories

import models.RecipeIngredients
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipeIngredientsRepository : JpaRepository<RecipeIngredients, Long>