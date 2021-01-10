package services

import models.Challenge
import models.UserChallenge
import org.springframework.stereotype.Service
import pojos.ChallengeUploadForm
import repositories.ChallengeRepository
import repositories.RecipeRepository
import repositories.UserChallengeRepository
import java.security.Principal
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import javax.transaction.Transactional


@Service
class ChallengeService(val challengeRepository: ChallengeRepository,
                       val userChallengeRepository: UserChallengeRepository,
                       val suggestionService: SuggestionService,
                       val fileService: FileService,
                       val userService: UserService,
                       val recipeRepository: RecipeRepository) {
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

    /**
     * Returns the Duration between now and the challenge creation date in seconds
     * */
    fun timeLeft(challenge: Challenge): Long {
        val created = LocalDateTime.from(challenge.createdAt.toInstant().atZone(ZoneId.systemDefault()))
        return Duration.between(created, LocalDateTime.now()).seconds
    }

    /**
     * saves a userChallenge by using @validated challengeUploadForm pojo
     * */
    @Transactional
    fun challengeUpload(principal: Principal, challengeUploadForm: ChallengeUploadForm, challengeId: Long): Challenge {
        val challenge = challengeRepository.findById(challengeId).get()
        val user = userService.findByUsername(principal.name)
        val file = fileService.trySaveMultipartFile(challengeUploadForm.file)

        if (challengeUploadForm.experience != null && file != null) {
            val userChallenge = UserChallenge(user, challenge, challengeUploadForm.experience, file)
            challenge.userChallenges.add(userChallenge)
            userChallengeRepository.save(userChallenge)
        } else {
            throw Error("Error saving UserChallenge to challenge with id $challengeId: file or experience is null!")
        }

        return challengeRepository.save(challenge)
    }

}