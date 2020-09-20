package validators

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import pojos.AddIngredientForm

@Component
class AddIngredientFormValidator : Validator {
    override fun validate(target: Any, errors: Errors) {
        val addIngredientForm = target as AddIngredientForm
        fileMimeTypeValid(errors, addIngredientForm)
    }

    override fun supports(aClass: Class<*>): Boolean {
        return AddIngredientForm::class.java == aClass
    }

    fun fileMimeTypeValid(errors: Errors, addIngredientForm: AddIngredientForm) {
        FileValidator.fileMimeTypeValid(errors, addIngredientForm.file, "file")
    }
}