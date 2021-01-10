package repositories

import models.UserChallenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserChallengeRepository : JpaRepository<UserChallenge, Long> {

    @Query("SELECT challenge FROM UserChallenge challenge WHERE challenge.embeddedKey.challenge.recipe.id = :recipeId")
    fun findByRecipeId(@Param("recipeId") recipeId: Long): List<UserChallenge>
}