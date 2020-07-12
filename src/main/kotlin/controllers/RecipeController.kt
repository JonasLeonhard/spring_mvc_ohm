package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import services.JsoupService
import services.RecipeService

@Controller
@RequestMapping("/recipe")
class RecipeController(val recipeService: RecipeService, val jsoupService: JsoupService) {


    /**
     * @param recipeId is the api id primary key, not the own db primary key
     */
    @GetMapping("/{id}")
    fun getRecipe(@PathVariable("id") recipeId: Long, @RequestParam(value = "cached") cached: Boolean?, model: Model): String {
        val recipe = if (cached != null && cached == false) {
            recipeService.getAndSaveRecipeFromRecipeApiId(recipeId)
        } else {
            recipeService.getIndexedRecipeById(recipeId)
        }

        model["pageTitle"] = recipe.title
        model["recipe"] = recipe
        model["escapedRecipeSummary"] = jsoupService.escapeUserText(recipe.summary)
        return "recipe"
    }
}