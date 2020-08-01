package repositories

import models.UserLikedRecipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserLikedRecipeRepository : JpaRepository<UserLikedRecipe, Long>