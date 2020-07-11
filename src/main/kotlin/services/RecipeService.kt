package services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import configurations.ApplicationPropertiesConfiguration
import models.Ingredient
import models.Recipe
import models.RecipeIngredients
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import pojos.RecipeSummary
import repositories.IngredientRepository
import repositories.RecipeIngredientsRepository
import repositories.RecipeRepository

@Service
class RecipeService(val props: ApplicationPropertiesConfiguration,
                    val objectMapper: ObjectMapper,
                    val recipeRepository: RecipeRepository,
                    val ingredientRepository: IngredientRepository,
                    val recipeIngredientsRepository: RecipeIngredientsRepository) {

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
    fun searchApi(q: String): MutableList<RecipeSummary> {
        val dbRecipes = searchRecipeSummariesInDatabase(q)
        val recipeSummaries = searchRecipeSummariesSpoonacular(q, 2)
        dbRecipes.addAll(recipeSummaries)
        return dbRecipes
    }

    fun saveRecipe(recipe: Recipe): Recipe {
        return recipeRepository.save(recipe)
    }

    fun saveIngredient(ingredient: Ingredient): Ingredient {
        return ingredientRepository.save(ingredient)
    }

    fun saveRecipeIngredients(recipeIngredient: RecipeIngredients): RecipeIngredients {
        return recipeIngredientsRepository.save(recipeIngredient)
    }

    private fun searchRecipeSummariesSpoonacular(q: String, amount: Int): MutableList<RecipeSummary> {
        val jsonResponseBody = spoonacularWebClient.get().uri { builder ->
            builder.path("/recipes/search")
                    .queryParam("apiKey", props.ENV_SPOONACULAR_API_KEY)
                    .queryParam("query", q)
                    .queryParam("number", amount)
                    .queryParam("addRecipeInformation", true)
                    .build()
        }.retrieve().bodyToMono<JsonNode>(JsonNode::class.java).block()

        println("got info::: $jsonResponseBody")
        return if (jsonResponseBody != null) {
            parseJsonToRecipeSummaries(jsonResponseBody)
        } else {
            mutableListOf()
        }
    }

    private fun searchRecipesInDatabase(q: String): MutableList<Recipe> {
        return this.recipeRepository.searchForRecipe(q)
    }

    private fun searchRecipeSummariesInDatabase(q: String): MutableList<RecipeSummary> {
        val recipes = searchRecipesInDatabase(q)
        val recipeSummaries = mutableListOf<RecipeSummary>()
        recipes.forEach { recipe ->
            recipeSummaries.add(RecipeSummary(
                    id = recipe.id,
                    title = recipe.title,
                    readyInMinutes = recipe.readyInMinutes,
                    servings = recipe.servings,
                    cached = true
            ))
        }
        return recipeSummaries
    }

    private fun getRecipeJsonFromRecipeApiId(id: Long): JsonNode? {
        return spoonacularWebClient.get().uri { builder ->
            builder.path("/recipes/${id}/information")
                    .queryParam("apiKey", props.ENV_SPOONACULAR_API_KEY)
                    .queryParam("includeNutrition", true)
                    .queryParam("instructionsRequired", true)
                    .queryParam("limitLicense", true)
                    .build()
        }.retrieve().bodyToMono<JsonNode>(JsonNode::class.java).block()
    }

    private fun saveRecipeJson(json: JsonNode): Recipe {
        val parsedRecipe = Recipe(
                instructions = json.get("instructions").asText(),
                readyInMinutes = json.get("readyInMinutes").asInt(),
                servings = json.get("servings").asInt(),
                spoonacularId = json.get("id").asLong(),
                summary = trimToMaxCharNum(json.get("summary").asText(), 255),
                title = json.get("title").asText()
        )
        val savedRecipe = saveRecipe(parsedRecipe)
        saveRecipeJsonIngredients(json, savedRecipe)
        return savedRecipe
    }

    private fun saveRecipeJsonIngredients(json: JsonNode, recipe: Recipe) {
        json.get("extendedIngredients").forEach { ingredient ->
            val parsedIngredient = Ingredient(
                    aisle = trimToMaxCharNum(ingredient.get("aisle").asText(), 255),
                    consistency = trimToMaxCharNum(ingredient.get("consistency").asText(), 255),
                    meta = trimToMaxCharNum(ingredient.get("meta").asText(), 255),
                    name = trimToMaxCharNum(ingredient.get("name").asText(), 255),
                    summary = trimToMaxCharNum(ingredient.get("original").asText(), 255),
                    unit = ingredient.get("unit").asText()
            )

            val savedIngredient = saveIngredient(parsedIngredient)
            val recipeIngredients = RecipeIngredients(
                    amount = ingredient.get("amount").asDouble(),
                    ingredient = savedIngredient,
                    recipe = recipe
            )
            saveRecipeIngredients(recipeIngredients)
        }
    }

    private fun parseJsonToRecipeSummaries(jsonNode: JsonNode): MutableList<RecipeSummary> {
        val parsedRecipes: MutableList<RecipeSummary> = mutableListOf()
        jsonNode.get("results").forEach { jsonRecipe ->
            val parsedRecipeSummary = objectMapper.treeToValue(jsonRecipe, RecipeSummary::class.java)
            parsedRecipes.add(parsedRecipeSummary)
        }
        return parsedRecipes
    }

    fun getIndexedRecipeByApiId(recipeSummaryId: Long): Recipe? {
        return this.recipeRepository.getIndexedRecipeByApiId(recipeSummaryId)
    }

    @Throws(NoSuchElementException::class)
    fun getIndexedRecipeById(recipeSummaryId: Long): Recipe {
        return this.recipeRepository.findById(recipeSummaryId).get()
    }


    /**
     * Returns a Recipe by its api id
     * @param id Query id to get recipe from api
     */
    @Transactional
    fun getAndSaveRecipeFromRecipeApiId(id: Long): Recipe {
        val json = getRecipeJsonFromRecipeApiId(id)
                ?: throw Exception("Couldn't get json response from Api")
        return saveRecipeJson(json)
    }

    private fun trimToMaxCharNum(string: String, maxChars: Int): String {
        return if (string.length > maxChars) {
            string.substring(0, maxChars + 1)
        } else {
            string
        }
    }
}