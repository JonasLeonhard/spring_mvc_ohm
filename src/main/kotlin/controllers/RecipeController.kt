package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import services.JsoupService
import services.RecipeService
import services.UserService
import java.security.Principal

@Controller
@RequestMapping("/recipe")
class RecipeController(val recipeService: RecipeService,
                       val jsoupService: JsoupService,
                       val userService: UserService) {


    /**
     * @param recipeId is the api id primary key, not the own db primary key
     */
    @GetMapping("/{id}")
    fun getRecipe(principal: Principal?, @PathVariable("id") recipeId: Long, model: Model): String {
        println("IN gET RECiPE:::::: $recipeId")
        val recipe = recipeService.getIndexedRecipeById(recipeId)
        userService.addAuthenticatedUserToModel(principal, model)
        model["pageTitle"] = recipe.title
        model["recipe"] = recipe
        model["escapedRecipeSummary"] = jsoupService.escapeUserText(recipe.summary)
        return "recipe"
    }

    @PostMapping("/like")
    fun likeRecipe(principal: Principal, @RequestParam(name = "recipeId") recipeId: Long): String {
        println("post to /like!!!!!!!!!!!!!!!!!!!!!!!!!!!!! $recipeId")
        userService.likeRecipe(recipeId, principal)
        val savedRecipeIS = recipeService.getIndexedRecipeById(recipeId)
        println("....... liking recipe succesful! ------ return recipe template!!!!! $savedRecipeIS")
        return "index"
    }

    @PostMapping("/{id}/favorite")
    fun favoriteRecipe(principal: Principal, @PathVariable("id") recipeId: Long, model: Model): String {
        println("favorite recipe $recipeId")
        return "recipe"
    }

    @PostMapping("/{id}/toList")
    fun toListRecipe(principal: Principal, @PathVariable("id") recipeId: Long, model: Model): String {
        println("tolist recipe $recipeId")
        return "recipe"
    }
}