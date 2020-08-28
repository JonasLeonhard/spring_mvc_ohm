package pojos

data class RecipeForm(
        val title: String? = null,
        val servings: Int? = null,
        val readyInMinutes: Int? = null,
        val preparationMinutes: Int? = null,
        val pricePerServing: Double? = null,
        val glutenFree: Boolean = false,
        val vegan: Boolean = false,
        val vegetarian: Boolean = false,
        val dairyFree: Boolean = false,
        val veryHealthy: Boolean = false,
        val cheap: Boolean = false,
        val sustainable: Boolean = false,
        val instructions: String? = null,
        val summary: String? = null,
        val recipeIngredients: String? = null
)