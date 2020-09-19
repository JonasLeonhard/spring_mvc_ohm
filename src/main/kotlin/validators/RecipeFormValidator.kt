package validators

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import pojos.RecipeForm

@Component
class RecipeFormValidator : Validator {
    override fun validate(target: Any, errors: Errors) {
        val recipeForm = target as RecipeForm
        canBuildIngredientsFromLists(errors, recipeForm)
        ingredientValuesNotNull(errors, recipeForm)
        fileExists(errors, recipeForm)
        fileMimeTypeValid(errors, recipeForm)
    }

    override fun supports(aClass: Class<*>): Boolean {
        return RecipeForm::class.java == aClass
    }

    fun canBuildIngredientsFromLists(errors: Errors, recipeForm: RecipeForm) {
        if (recipeForm.ingredientsAmount.isNullOrEmpty()) {
            errors.rejectValue("ingredientsAmount", "AmountError", "Field 'ingredientsAmount' cannot be null or empty")
        }
        if (recipeForm.ingredientsAisle.isNullOrEmpty()) {
            errors.rejectValue("ingredientsAisle", "AisleError", "Field 'ingredientsAisle' cannot be null or empty")
        }
        if (recipeForm.ingredientsConsistency.isNullOrEmpty()) {
            errors.rejectValue("ingredientsConsistency", "ConsistencyError", "Field 'ingredientsConsistency' cannot be null or empty")
        }
        if (recipeForm.ingredientsMeta.isNullOrEmpty()) {
            errors.rejectValue("ingredientsMeta", "MetaError", "Field 'ingredientsMeta' cannot be null or empty")
        }

        if (recipeForm.ingredientsSummary.isNullOrEmpty()) {
            errors.rejectValue("ingredientsSummary", "SummaryError", "Field 'ingredientsSummary' cannot be null or empty")
        }

        if (recipeForm.ingredientsName.isNullOrEmpty()) {
            errors.rejectValue("ingredientsName", "NameError", "Field 'ingredientsName' cannot be null or empty")
        }
        if (recipeForm.ingredientsUnit.isNullOrEmpty()) {
            errors.rejectValue("ingredientsUnit", "UnitError", "Field 'ingredientsUnit' cannot be null or empty")
        }

        val baseLength = recipeForm.ingredientsName?.size
        if (baseLength != recipeForm.ingredientsAisle?.size) {
            errors.rejectValue("ingredientsAisle", "SizeError", "Field 'ingredientsAisle' has to have as many entries as ingredientsName")
        }

        if (baseLength != recipeForm.ingredientsAmount?.size) {
            errors.rejectValue("ingredientsAmount", "SizeError", "Field 'ingredientsAmount' has to have as many entries as ingredientsName")
        }

        if (baseLength != recipeForm.ingredientsConsistency?.size) {
            errors.rejectValue("ingredientsConsistency", "SizeError", "Field 'ingredientsConsistency' has to have as many entries as ingredientsName")
        }

        if (baseLength != recipeForm.ingredientsMeta?.size) {
            errors.rejectValue("ingredientsMeta", "SizeError", "Field 'ingredientsMeta' has to have as many entries as ingredientsName")
        }

        if (baseLength != recipeForm.ingredientsSummary?.size) {
            errors.rejectValue("ingredientsSummary", "SizeError", "Field 'ingredientsSummary' has to have as many entries as ingredientsName")
        }

        if (baseLength != recipeForm.ingredientsUnit?.size) {
            errors.rejectValue("ingredientsUnit", "SizeError", "Field 'ingredientsUnit' has to have as many entries as ingredientsName")
        }
    }

    fun ingredientValuesNotNull(errors: Errors, recipeForm: RecipeForm) {
        recipeForm.ingredientsName?.forEachIndexed { index, name ->
            if (name.isNullOrEmpty()) {
                errors.rejectValue("ingredientsName", "NameError_$index", "err_on_$index")
            }
        }

        recipeForm.ingredientsUnit?.forEachIndexed { index, unit ->
            if (unit.isNullOrEmpty()) {
                errors.rejectValue("ingredientsUnit", "UnitError_$index", "err_on_$index")
            }
        }

        recipeForm.ingredientsSummary?.forEachIndexed { index, sum ->
            if (sum.isNullOrEmpty()) {
                errors.rejectValue("ingredientsSummary", "SummaryError_$index", "err_on_$index")
            }
        }

        recipeForm.ingredientsMeta?.forEachIndexed { index, meta ->
            if (meta.isNullOrEmpty()) {
                errors.rejectValue("ingredientsMeta", "MetaError_$index", "err_on_$index")
            }
        }

        recipeForm.ingredientsConsistency?.forEachIndexed { index, con ->
            if (con.isNullOrEmpty()) {
                errors.rejectValue("ingredientsConsistency", "ConsistencyError_$index", "err_on_$index")
            }
        }

        recipeForm.ingredientsAmount?.forEachIndexed { index, amount ->
            if (amount == null) {
                errors.rejectValue("ingredientsAmount", "IngredientsError_$index", "err_on_$index")
            }
        }

        recipeForm.ingredientsAisle?.forEachIndexed { index, aisle ->
            if (aisle.isNullOrEmpty()) {
                errors.rejectValue("ingredientsAisle", "AisleError_$index", "err_on_$index")
            }
        }

    }

    fun fileMimeTypeValid(errors: Errors, recipeForm: RecipeForm) {
        FileValidator.fileMimeTypeValid(errors, recipeForm.file, "file")
    }

    fun fileExists(errors: Errors, recipeForm: RecipeForm) {
        println("file exists $recipeForm")
        if (recipeForm.file == null) {
            errors.rejectValue("file", "FileError", "Field file is missing")
        }

        if (recipeForm.file != null && recipeForm.file!!.isEmpty) {
            errors.rejectValue("file", "FileError", "Field file is empty")
        }
    }
}