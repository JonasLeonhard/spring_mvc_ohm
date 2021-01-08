package services

import models.Challenge
import org.springframework.stereotype.Service
import repositories.ChallengeRepository

@Service
class ChallengeService(val challengeRepository: ChallengeRepository,
                       val suggestionService: SuggestionService) {
    /***
     * This is called once per day and changes the current cooking challenge
     * sets all current dayChallegnes to false
     * creates a new random getRecipeOfTheDaySuggestion() challenge
     * this is called once per day in ChronjobConfiguration
     * */
    fun changeChallenge(): Challenge {
        val updatedChallenges: List<Challenge> = challengeRepository.getDayChallenges().map { challenge ->
            challenge.dayChallenge = false
            return challenge
        }
        challengeRepository.saveAll(updatedChallenges)

        val dayChallenge = Challenge(recipe = suggestionService.getRecipeOfTheDaySuggestion(), dayChallenge = true)

        return challengeRepository.save(dayChallenge)
    }

    /**
     * Gets the curretn Challenge of the day or creates a new one if it doesnt exist
     */
    fun getChallengeOfTheDay(): Challenge {
        val challenges = challengeRepository.getDayChallenges()

        return if (challenges.isEmpty()) {
            changeChallenge()
        } else {
            challenges[0]
        }
    }

}