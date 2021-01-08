package repositories

import models.Challenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChallengeRepository : JpaRepository<Challenge, Long> {

    @Query("SELECT challenge FROM Challenge challenge WHERE challenge.dayChallenge = true")
    fun getDayChallenges(): MutableList<Challenge>
}