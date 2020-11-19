package pojos

import models.Recipe

data class MostLikedRecipeProjection(
        val recipe: Recipe,
        val userLikes: Long
)