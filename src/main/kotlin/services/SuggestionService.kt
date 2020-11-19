package services

import models.Recipe
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import repositories.RecipeRepository
import kotlin.random.Random

@Service
class SuggestionService(val recipeRepository: RecipeRepository) {
    fun getIndexSuggestions(): MutableList<Recipe> {
        val suggestions = mutableListOf<Recipe>()

        suggestions.addAll(recipeRepository.getNewestRecipes(PageRequest.of(0, 10)))
        suggestions.addAll(recipeRepository.getMostLikedRecipes(PageRequest.of(0, 25)).map { it.recipe })
        val recipeIds = recipeRepository.getRecipeIds()
        val randomRecipeIds = (0..25).map {
            recipeIds[Random.nextInt(recipeIds.size)]
        }
        suggestions.addAll(recipeRepository.findAllById(randomRecipeIds))

        val randomizedUniqueSuggestions = suggestions.distinctBy { recipe ->
            recipe.id
        }.toMutableList()
        randomizedUniqueSuggestions.shuffle()
        return randomizedUniqueSuggestions
    }

    fun getRecipeOfTheDaySuggestion(): Recipe {
        val recipeIds = recipeRepository.getRecipeIds()
        val randomId = recipeIds[Random.nextInt(recipeIds.size)]
        return recipeRepository.findById(randomId).get()
    }
}