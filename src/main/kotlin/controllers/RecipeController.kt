package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pojos.RecipeForm
import services.ChallengeService
import services.RecipeService
import services.UserService
import validators.RecipeFormValidator
import java.security.Principal
import java.text.SimpleDateFormat
import javax.validation.Valid

@Controller
@RequestMapping("/recipe")
class RecipeController(val recipeService: RecipeService,
                       val recipeFormValidator: RecipeFormValidator,
                       val challengeService: ChallengeService,
                       val userService: UserService) {


    @GetMapping("/{id}")
    fun getRecipe(principal: Principal?, @PathVariable("id") recipeId: Long, model: Model): String {
        val recipe = recipeService.getIndexedRecipeById(recipeId)
        val user = userService.addAuthenticatedUserToModel(principal, model)

        model["pageTitle"] = "${recipe.title} | F&F"
        model["recipe"] = recipe
        model["recipeComments"] = recipeService.getComments(recipe)
        model["dateFormatter"] = SimpleDateFormat("dd/MM/yyyy hh:mm")
        model["userChallenges"] = challengeService.getRecipeUserChallenges(recipeId)
        println("userChallenges ${challengeService.getRecipeUserChallenges(recipeId)}")
        if (user != null) {
            model["userLikedRecipe"] = user.hasLikedRecipe(recipe)
            model["userFavoritedRecipe"] = user.hasFavoritedRecipe(recipe)
            model["userBuyListedRecipe"] = user.hasBuyListedRecipe(recipe)
        }
        return "recipe"
    }

    @GetMapping("/create")
    fun createRecipePage(principal: Principal, recipeForm: RecipeForm?, @RequestParam subtractIngredient: Boolean?, model: Model): String {
        model["pageTitle"] = "Create a Recipe | F&F"
        userService.addAuthenticatedUserToModel(principal, model)

        if (recipeForm != null) {
            if (subtractIngredient == true && recipeForm.ingredientsName != null && recipeForm.ingredientsName.size > 1) {
                recipeForm.ingredientsName.removeAt(recipeForm.ingredientsName.size - 1)
                recipeForm.ingredientsAisle?.removeAt(recipeForm.ingredientsAisle.size - 1)
                recipeForm.ingredientsAmount?.removeAt(recipeForm.ingredientsAmount.size - 1)
                recipeForm.ingredientsConsistency?.removeAt(recipeForm.ingredientsConsistency.size - 1)
                recipeForm.ingredientsMeta?.removeAt(recipeForm.ingredientsMeta.size - 1)
                recipeForm.ingredientsSummary?.removeAt(recipeForm.ingredientsSummary.size - 1)
                recipeForm.ingredientsUnit?.removeAt(recipeForm.ingredientsUnit.size - 1)
            } else if (subtractIngredient != true) {
                recipeForm.ingredientsName?.add(null)
                recipeForm.ingredientsAisle?.add(null)
                recipeForm.ingredientsAmount?.add(null)
                recipeForm.ingredientsConsistency?.add(null)
                recipeForm.ingredientsMeta?.add(null)
                recipeForm.ingredientsSummary?.add(null)
                recipeForm.ingredientsUnit?.add(null)
            }
            model["recipeForm"] = recipeForm
        }

        if (recipeForm?.ingredientsName == null) {
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
                    ingredientsName = mutableListOf("ab"),
                    ingredientsMeta = mutableListOf("meta1"),
                    ingredientsSummary = mutableListOf("sum1"),
                    ingredientsAisle = mutableListOf("aisle1"),
                    ingredientsAmount = mutableListOf(10.0),
                    ingredientsConsistency = mutableListOf("consistency1"),
                    ingredientsUnit = mutableListOf("unit1")
            )
        }

        model["ingredientsAmount"] = if (recipeForm?.ingredientsName == null) {
            0
        } else {
            recipeForm.ingredientsName.size
        }
        return "createRecipe"
    }

    @PostMapping("/create")
    fun createRecipe(principal: Principal, @Valid recipeForm: RecipeForm, bindingResult: BindingResult, @RequestParam formFile: MultipartFile?, model: Model): String {
        recipeForm.file = formFile
        recipeFormValidator.validate(recipeForm, bindingResult)
        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
            model["recipeForm"] = recipeForm
            model["ingredientsAmount"] = if (recipeForm.ingredientsName == null) {
                0
            } else {
                recipeForm.ingredientsName.size
            }
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

    @PostMapping("/{id}/comment")
    fun commentOn(principal: Principal, @PathVariable("id") recipeId: Long, @RequestParam(name = "message") message: String): String {
        println("commentON $recipeId $message")
        userService.commentRecipe(recipeId, principal, message)
        return "redirect:/recipe/$recipeId#comments"
    }

    @PostMapping("/{id}/saveBuyList")
    fun saveBuyList(principal: Principal, @PathVariable("id") recipeId: Long, @RequestParam(name = "ingredientId") ingredientIds: List<Long>?): String {
        userService.saveRecipeBuyList(principal, recipeId, ingredientIds ?: mutableListOf())
        return "redirect:/user/buyList"
    }
}