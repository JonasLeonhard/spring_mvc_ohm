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
        model["pageTitle"] = "INDEXPAGETITLE"
        model["suggestions"] = suggestionService.getIndexSuggestions()
        model["recipeOfTheDay"] = suggestionService.getRecipeOfTheDaySuggestion()
        model["mostPopularSearch"] = searchService.getMostPopularSearchTerms(10)
        model["mostRecentSearch"] = searchService.getRecentSearchTerms(10)

        val challenge = challengeService.getChallengeOfTheDay()
        model["challengeOfTheDay"] = challenge
        model["timeLeft"] = challengeService.timeLeft(challenge)

        userService.addAuthenticatedUserToModel(principal, model)

        return "index"
    }
}