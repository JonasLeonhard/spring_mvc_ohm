package pojos

import models.Recipe

data class RecipeSuggestions(
        val recipe: Recipe,
        val ingredientMatchingCount: Long
)