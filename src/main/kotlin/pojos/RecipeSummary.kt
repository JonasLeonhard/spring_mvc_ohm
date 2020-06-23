package pojos

import com.fasterxml.jackson.annotation.JsonIgnore

data class RecipeSummary(
        var id: Long,
        var title: String,
        var readyInMinutes: Int,
        var servings: Int,
        var sourceUrl: String? = null,
        var openLicense: Int? = null,
        var image: String? = null,

        @JsonIgnore
        var isDatabaseRecipe: Boolean = false
)