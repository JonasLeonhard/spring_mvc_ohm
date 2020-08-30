package pojos

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class RecipeForm(
        @field:NotBlank(message = "Title cannot be empty")
        @field:NotNull(message = "Title has to be specified")
        val title: String? = null,

        @field:NotNull(message = "Servings has to be specified")
        @field:Positive(message = "Servings has to be positive")
        val servings: Int? = null,

        @field:NotNull(message = "Ready in Minutes has to be specified")
        @field:Positive(message = "Ready in Minutes has to be positive")
        val readyInMinutes: Int? = null,

        @field:NotNull(message = "Preparation Minutes has to be specified")
        @field:Positive(message = "Preparartion Minutes has to be positive")
        val preparationMinutes: Int? = null,

        @field:NotNull(message = "Price per serving has to be specified")
        @field:Positive(message = "Price per serving has to be positive")
        val pricePerServing: Double? = null,

        @field:NotNull(message = "Gluten free has to be specified")
        val glutenFree: Boolean? = null,

        @field:NotNull(message = "Vegan has to be specified")
        val vegan: Boolean? = null,

        @field:NotNull(message = "Vegetarian has to be specified")
        val vegetarian: Boolean? = null,

        @field:NotNull(message = "Dairy free has to be specified")
        val dairyFree: Boolean? = null,

        @field:NotNull(message = "Very healthy has to be specified")
        val veryHealthy: Boolean? = null,

        @field:NotNull(message = "Cheap has to be specified")
        val cheap: Boolean? = null,

        @field:NotNull(message = "Sustainable has to be specified")
        val sustainable: Boolean? = null,

        @field:NotBlank(message = "Instructions cannot be empty")
        @field:NotNull(message = "Instruction have to be specified")
        val instructions: String? = null,

        @field:NotBlank(message = "Summary cannot be empty")
        @field:NotNull(message = "Summary has to be specified")
        val summary: String? = null,

        /* INGREDIENTS */
        @field:NotNull(message = "Ingredient names have to be specified")
        val ingredientsName: List<String?>? = null,

        @field:NotNull(message = "Ingredient meta info's have to be specified")
        val ingredientsMeta: List<String?>? = null,

        @field:NotNull(message = "Ingredient aisle's have to be specified")
        val ingredientsAisle: List<String?>? = null,

        @field:NotNull(message = "Ingredient consistencies have to be specified")
        val ingredientsConsistency: List<String?>? = null,

        @field:NotNull(message = "Ingredient unit's have to be specified")
        val ingredientsUnit: List<String?>? = null,

        @field:NotNull(message = "Ingredient amount's have to be specified")
        val ingredientsAmount: List<Double?>? = null
)