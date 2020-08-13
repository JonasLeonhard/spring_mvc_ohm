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
        val recipe = recipeService.getIndexedRecipeById(recipeId)
        val user = userService.addAuthenticatedUserToModel(principal, model)

        model["pageTitle"] = recipe.title
        model["recipe"] = recipe
        model["escapedRecipeSummary"] = jsoupService.escapeUserText(recipe.summary)

        if (user != null) {
            model["userLikedRecipe"] = user.hasLikedRecipe(recipe)
            model["userFavoritedRecipe"] = user.hasFavoritedRecipe(recipe)
            model["userBuyListedRecipe"] = user.hasBuyListedRecipe(recipe)
        }
        return "recipe"
    }

    @PostMapping("/like")
    fun likeRecipe(principal: Principal, @RequestParam(name = "recipeId") recipeId: Long): String {
        userService.likeRecipe(recipeId, principal)
        return "redirect:/recipe/$recipeId"
    }

    @PostMapping("/{id}/favorite")
    fun favoriteRecipe(principal: Principal, @PathVariable("id") recipeId: Long, model: Model): String {
        userService.favoriteRecipe(recipeId, principal)
        return "redirect:/recipe/$recipeId"
    }

    @PostMapping("/{id}/toList")
    fun toListRecipe(principal: Principal, @PathVariable("id") recipeId: Long, model: Model): String {
        userService.addRecipeToBuyList(recipeId, principal)
        return "redirect:/recipe/$recipeId"
    }
}