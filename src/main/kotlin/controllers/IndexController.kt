package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import services.ChallengeService
import services.SearchService
import services.SuggestionService
import services.UserService
import java.security.Principal


@Controller
@RequestMapping("/")
class IndexController(val userService: UserService,
                      val suggestionService: SuggestionService,
                      val searchService: SearchService,
                      val challengeService: ChallengeService) {
    @GetMapping
    fun index(model: Model, principal: Principal?): String {
        model["pageTitle"] = "Welcome | F&F"
        model["suggestions"] = suggestionService.getIndexSuggestions()
        val recipeOfTheDay = suggestionService.getRecipeOfTheDaySuggestion()
        if (recipeOfTheDay != null) {
            model["recipeOfTheDay"] = recipeOfTheDay
        }
        model["mostPopularSearch"] = searchService.getMostPopularSearchTerms(10)
        model["mostRecentSearch"] = searchService.getRecentSearchTerms(10)

        val challenge = challengeService.getChallengeOfTheDay()
        if (challenge != null) {
            model["challengeOfTheDay"] = challenge
            model["timeLeft"] = challengeService.timeLeft(challenge)
        }
        userService.addAuthenticatedUserToModel(principal, model)

        return "index"
    }
}