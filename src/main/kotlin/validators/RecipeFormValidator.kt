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
        errors.rejectValue("summary", "SummaryError", "this is a summary error")
    }
}