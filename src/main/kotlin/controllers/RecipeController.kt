package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import pojos.RecipeForm
import services.JsoupService
import services.RecipeService
import services.UserService
import validators.RecipeFormValidator
import java.security.Principal
import javax.validation.Valid

@Controller
@RequestMapping("/recipe")
class RecipeController(val recipeService: RecipeService,
                       val recipeFormValidator: RecipeFormValidator,
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

    @GetMapping("/create")
    fun createRecipePage(principal: Principal, model: Model): String {
        userService.addAuthenticatedUserToModel(principal, model)
        model["recipeForm"] = RecipeForm(
                title = "THIS IS A TEST TITLE",
                servings = 2,
                readyInMinutes = 20,
                preparationMinutes = 15,
                pricePerServing = 12.0,
                glutenFree = false,
                vegan = true,
                vegetarian = true,
                veryHealthy = false,
                sustainable = false,
                dairyFree = false,
                cheap = true,
                instructions = "THIS IS A TEST INSTRUCTION",
                summary = "THIS IS A TEST SUMMARY",
                ingredientsName = listOf("ab", "bc"),
                ingredientsMeta = listOf("meta1", "meta2"),
                ingredientsAisle = listOf("aisle1", "aisle2"),
                ingredientsAmount = listOf(10.0, 2.0),
                ingredientsConsistency = listOf("consistency1", "consistency2"),
                ingredientsUnit = listOf("unit1", "unit2")
        )

        return "createRecipe"
    }

    @PostMapping("/create")
    fun createRecipe(principal: Principal, @Valid @ModelAttribute recipeForm: RecipeForm, bindingResult: BindingResult, model: Model): String {
        recipeFormValidator.validate(recipeForm, bindingResult)
        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
            model["recipeForm"] = recipeForm
            return "createRecipe"
        }

        val recipe = recipeService.createRecipeFromForm(recipeForm, principal)
        return "redirect:/recipe/${recipe.id}"
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
    fun toListRecipe(principal: Principal, @PathVariable("id") recipeId: Long, model: Model, @RequestParam(required = false, name = "redirectTo") redirectTo: String?): String {
        userService.addRecipeToBuyList(recipeId, principal)
        return if (redirectTo == null) "redirect:/recipe/$recipeId" else "redirect:$redirectTo"
    }
}