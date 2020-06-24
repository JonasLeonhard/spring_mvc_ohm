package pojos

data class RecipeSummary(
        var id: Long,
        var title: String,
        var readyInMinutes: Int,
        var servings: Int,
        var sourceUrl: String? = null,
        var openLicense: Int? = null,
        var image: String? = null,

        var cached: Boolean = false
)