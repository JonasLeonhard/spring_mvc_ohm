package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import services.RecipeService

@Controller
@RequestMapping("/recipe")
class RecipeController(val recipeService: RecipeService) {


    /**
     * @param recipeId is the api id primary key, not the own db primary key
     */
    @GetMapping("/{id}")
    fun getRecipe(@PathVariable("id") recipeId: Long, @RequestParam(value = "cached") cached: Boolean, model: Model): String {
        model["pageTitle"] = "Recipe called! fromDB= $cached"
        //TODO - get the recipe from recipeService from the database
        if (cached) {
            model["recipe"] = recipeService.getIndexedRecipeById(recipeId)
        } else {
            model["recipe"] = recipeService.getAndSaveRecipeFromRecipeApiId(recipeId)
        }
        return "recipe"
    }
}