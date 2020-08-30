package validators

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import pojos.RecipeForm

@Component
class RecipeFormValidator : Validator {
    override fun validate(target: Any, errors: Errors) {
        val recipeForm = target as RecipeForm
        something(errors, recipeForm)
    }

    override fun supports(aClass: Class<*>): Boolean {
        return RecipeForm::class.java == aClass
    }

    fun something(errors: Errors, recipeForm: RecipeForm) {
        // TODO
        // errors.reject
        println("validate recipeForm: $recipeForm")
        errors.rejectValue("summary", "SummaryError", "this is a summary error")
    }

    fun canBuildIngredientsFromLists(errors: Errors, recipeForm: RecipeForm) {
        // are all lists the same length?
        // are the values not null?
        TODO()
    }

    fun ingredientsName(errors: Errors, recipeForm: RecipeForm) {
        TODO()
    }

    fun ingredientsMeta(errors: Errors, recipeForm: RecipeForm) {
        TODO()
    }

    fun ingredientsAisle(errors: Errors, recipeForm: RecipeForm) {
        TODO()
    }

    fun ingredientsConsistency(errors: Errors, recipeForm: RecipeForm) {
        TODO()
    }

    fun ingredientsUnit(errors: Errors, recipeForm: RecipeForm) {
        TODO()
    }

    fun ingredientsAmount(errors: Errors, recipeForm: RecipeForm) {
        TODO()
    }


}