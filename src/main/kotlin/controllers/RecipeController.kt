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
        model["recipeForm"] = RecipeForm()
        return "createRecipe"
    }

    @PostMapping("/create", consumes = ["multipart/form-data"])
    fun createRecipe(principal: Principal, @Valid @ModelAttribute recipeForm: RecipeForm, bindingResult: BindingResult, model: Model): String {
        recipeFormValidator.validate(recipeForm, bindingResult)
        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
            model["recipeForm"] = recipeForm
            return "createRecipe"
        }

        // TODO: no errors: Create recipe here
        println("principal:: ${principal.name}")
        println("recipeForm :: $recipeForm... bingingResutl:: $bindingResult")

        // TODO: on success: redirect to recipe page
        return "redirect:/recipe/create"
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