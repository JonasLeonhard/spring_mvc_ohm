package services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import configurations.ApplicationPropertiesConfiguration
import models.Recipe
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import pojos.SpoonacularRecipeSummary
import repositories.RecipeRepository

@Service
class RecipeService(val props: ApplicationPropertiesConfiguration,
                    val objectMapper: ObjectMapper,
                    val recipeRepository: RecipeRepository) {

    private val spoonacularWebClient: WebClient = WebClient.builder()
            .baseUrl("https://api.spoonacular.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

    /**
     * Returns a List of *Recipe* model objects. By calling the spoonacular
     * /recipes/search endpoint with the specified query and searching already indexed recipes
     * in the database
     * @param q Query string to search the api & database for
     */
    fun searchApi(q: String): MutableList<Recipe> {
        val recipeSummaries = getRecipeSummariesSpoonacular(q)
        return TODO()
    }

    fun saveRecipe(recipe: Recipe): Recipe {
        return recipeRepository.save(recipe)
    }

    private fun getRecipeSummariesSpoonacular(q: String): MutableList<SpoonacularRecipeSummary> {
        val jsonResponseBody = spoonacularWebClient.get().uri { builder ->
            builder.path("/recipes/search")
                    .queryParam("apiKey", props.ENV_SPOONACULAR_API_KEY)
                    .queryParam("query", q)
                    .queryParam("number", 2)
                    .build()
        }.retrieve().bodyToMono<JsonNode>(JsonNode::class.java).block()

        return if (jsonResponseBody != null) {
            parseJsonToRecipeSummaries(jsonResponseBody)
        } else {
            mutableListOf()
        }
    }

    private fun parseJsonToRecipeSummaries(jsonNode: JsonNode): MutableList<SpoonacularRecipeSummary> {
        val parsedRecipes: MutableList<SpoonacularRecipeSummary> = mutableListOf()
        jsonNode.get("results").forEach { jsonRecipe ->
            val parsedRecipeSummary = objectMapper.treeToValue(jsonRecipe, SpoonacularRecipeSummary::class.java)
            parsedRecipes.add(parsedRecipeSummary)
        }
        return parsedRecipes
    }

    private fun getIndexedRecipe(recipeSummary: SpoonacularRecipeSummary): Recipe? {
        // This takes the recipeSummary and checkes if the recipe is already cached in the database
        // if yes it retrieves it, if not it returns null
        TODO()
    }

    private fun saveUnindexedRecipe(recipeSummary: SpoonacularRecipeSummary): Recipe {
        // this retrieves the full recipe from the spoonacular api and chaches it in the database
        TODO()
    }
}