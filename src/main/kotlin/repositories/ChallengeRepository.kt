package repositories

import models.Challenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChallengeRepository : JpaRepository<Challenge, Long> {

    @Query("SELECT challenge FROM Challenge challenge WHERE challenge.dayChallenge = true")
    fun getDayChallenges(): MutableList<Challenge>

    @Query("SELECT challenge FROM Challenge challenge WHERE challenge.recipe.id = :recipeId")
    fun findByRecipeId(@Param("recipeId") recipeId: Long): List<Challenge>
}