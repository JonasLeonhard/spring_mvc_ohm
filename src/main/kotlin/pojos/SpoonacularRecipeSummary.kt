package pojos

data class SpoonacularRecipeSummary(
        val id: Long,
        val title: String,
        val readyInMinutes: Int,
        val servings: Int,
        val sourceUrl: String,
        val openLicense: Int,
        val image: String
)