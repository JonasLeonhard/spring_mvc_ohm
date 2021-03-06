package services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import configurations.ApplicationPropertiesConfiguration
import models.Ingredient
import models.Recipe
import models.RecipeIngredients
import models.UserRecipeComment
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import pojos.RecipeForm
import pojos.RecipeSummary
import repositories.IngredientRepository
import repositories.RecipeIngredientsRepository
import repositories.RecipeRepository
import repositories.UserRecipeCommentRepository
import java.security.Principal
import java.util.*
import javax.transaction.Transactional
import kotlin.NoSuchElementException

@Service
class RecipeService(val props: ApplicationPropertiesConfiguration,
                    val objectMapper: ObjectMapper,
                    val recipeRepository: RecipeRepository,
                    val ingredientRepository: IngredientRepository,
                    val recipeIngredientsRepository: RecipeIngredientsRepository,
                    val userRecipeCommentRepository: UserRecipeCommentRepository,
                    val userService: UserService,
                    val fileService: FileService,
                    val jsoupService: JsoupService,
                    val searchService: SearchService) {

    private val spoonacularWebClient: WebClient = WebClient.builder()
            .baseUrl("https://api.spoonacular.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

    /**
     * Returns a List of *Recipe* model objects. By calling the spoonacular
     * /recipes/search endpoint with the specified query.
     * The returning list contains Recipes matching the query in the database aswell as newly recieved ones.
     * Also Caches recipe searches that return results in *Search table
     * @param q Query string to search the api & database for
     */
    fun search(q: String): MutableList<Recipe> {
        val dbRecipes = searchRecipesInDatabase(q)
        val recipeSummaries = searchRecipeSummariesSpoonacular(q, 2, 0)
        println("recipeSummaries $recipeSummaries")
        val recipesFromSummaries = bulkSearchRecipesFromSummaries(recipeSummaries, exclude = dbRecipes)
        val savedRecipes = saveBulkSearchRecipes(recipesFromSummaries)
        dbRecipes.addAll(savedRecipes)

        searchService.addSearch(q, dbRecipes.size)
        return dbRecipes
    }

    @Throws(NoSuchElementException::class)
    fun getIndexedRecipeById(recipeSummaryId: Long): Recipe {
        return this.recipeRepository.findById(recipeSummaryId).get()
    }

    fun saveRecipe(recipe: Recipe): Recipe {
        return recipeRepository.save(recipe)
    }

    fun saveIngredient(ingredient: Ingredient): Ingredient {
        val indexedIngredient = ingredientRepository.findIngredientByName(ingredient.name)
        if (indexedIngredient != null) {
            return indexedIngredient
        }
        return ingredientRepository.save(ingredient)
    }

    fun saveRecipeIngredients(recipeIngredient: RecipeIngredients): RecipeIngredients {
        return recipeIngredientsRepository.save(recipeIngredient)
    }

    /**
     * Retrieves a list of RecipeSummary Pojos from the api
     * @param q Query string to search the api for
     * @param amount amount of summaries to retrieve
     * @param offset the number of results to skip
     */
    private fun searchRecipeSummariesSpoonacular(q: String, amount: Int, offset: Int): MutableList<RecipeSummary> {
        try {
            return spoonacularWebClient.get().uri { builder ->
                builder.path("/recipes/search")
                        .queryParam("apiKey", props.ENV_SPOONACULAR_API_KEY)
                        .queryParam("query", q)
                        .queryParam("number", amount)
                        .queryParam("offset", offset)
                        .queryParam("addRecipeInformation", true)
                        .build()
            }.retrieve().bodyToMono<JsonNode>(JsonNode::class.java).block()
                    ?.get("results")?.asIterable()?.map { jsonRecipe ->
                        objectMapper.treeToValue(jsonRecipe, RecipeSummary::class.java)
                    }?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            println("Api_Exception: 'Connection Timeout' -> in Remote api call: 'fun searchRecipeSummariesSpoonacular()...' ")
            return mutableListOf()
        }
    }

    /**
     * Gets Recipe Details from searchRecipeSummariesSpoonacular() recipeSummaries
     * and excludes already cached recipes in the database,
     * @param summaries The recipeSummaries to recive full Recipe Information for
     * @param exclude Already cached recipes in the database that should not be included in the request
     * @return Returns null or a JsonNode Array of JsonNodes containing Recipe Information
     */
    fun bulkSearchRecipesFromSummaries(summaries: MutableList<RecipeSummary>, exclude: MutableList<Recipe>): JsonNode? {
        val commaSeparatedIds = getCommaSeparatedIds(summaries, exclude)
        if (commaSeparatedIds.isNotEmpty()) {
            return spoonacularWebClient.get().uri { builder ->
                builder.path("/recipes/informationBulk")
                        .queryParam("apiKey", props.ENV_SPOONACULAR_API_KEY)
                        .queryParam("ids", commaSeparatedIds)
                        .build()
            }.retrieve().bodyToMono<JsonNode>(JsonNode::class.java).block()
        } else {
            return null
        }
    }

    /**
     * Saves a bulk of recipes in the database and returns them
     * @param jsonNode an jsonNode array of jsonNodes containing recipe information
     * @return Returns a List of Recipe based on their respective summaries
     */
    private fun saveBulkSearchRecipes(jsonNode: JsonNode?): MutableList<Recipe> {
        val parsedRecipes = jsonNode?.asIterable()?.map { jsonNodeB ->
            saveRecipeJson(jsonNodeB)
        }?.toMutableList()

        return parsedRecipes ?: mutableListOf()
    }

    private fun searchRecipesInDatabase(q: String): MutableList<Recipe> {
        return this.recipeRepository.searchForRecipe(q)
    }

    /**
     * Saves a JsonNode of a single Recipe and its ingredients if they do not exists
     * @param json A JsonNode containig recipeInformation
     */
    @Transactional
    fun saveRecipeJson(json: JsonNode): Recipe {
        val parsedRecipe = Recipe(
                instructions = jsoupService.escapeUserText(json.get("instructions")?.asText() ?: ""),
                readyInMinutes = json.get("readyInMinutes")?.asInt() ?: 0,
                preparationMinutes = json.get("preparationMinutes")?.asInt() ?: 0,
                servings = json.get("servings")?.asInt() ?: 0,
                vegetarian = json.get("vegetarian")?.asBoolean() ?: false,
                vegan = json.get("vegan")?.asBoolean() ?: false,
                glutenFree = json.get("glutenFree")?.asBoolean() ?: false,
                dairyFree = json.get("dairyFree")?.asBoolean() ?: false,
                veryHealthy = json.get("veryHealthy")?.asBoolean() ?: false,
                cheap = json.get("cheap")?.asBoolean() ?: false,
                veryPopular = json.get("veryPopular")?.asBoolean() ?: false,
                sustainable = json.get("sustainable")?.asBoolean() ?: false,
                likes = json.get("aggregateLikes")?.asInt() ?: 0,
                pricePerServing = json.get("pricePerServing")?.asDouble() ?: 0.0,
                spoonacularId = json.get("id").asLong(),
                summary = (jsoupService.escapeUserText(json.get("summary")?.asText() ?: "")).split(". ").filter {
                    !(it.contains("spoonacular", ignoreCase = true) ||
                            it.contains("href=\"https://spoonacular.com", ignoreCase = true) ||
                            it.contains("score", ignoreCase = true) ||
                            it.contains("SmartPoints", ignoreCase = true) ||
                            it.contains("made this recipe", ignoreCase = true) ||
                            it.contains("liked", ignoreCase = true) ||
                            it.contains("calories", ignoreCase = true) ||
                            it.contains("people found", ignoreCase = true) ||
                            it.contains("daily requirements", ignoreCase = true))
                }.joinToString(),
                title = json.get("title").asText(),
                recipeImageUrl = json.get("image")?.asText()
        )
        val savedRecipe = saveRecipe(parsedRecipe)
        saveRecipeJsonIngredients(json, savedRecipe)
        return savedRecipe
    }

    /**
     * Loops through all "extendedIngredients" key value pairs of a json node and saves it to a given recipe
     * @param json Json containing recipe ingredients as the key "extendedIngredients"
     * @param recipe The recipe the ingredients are related (saved) to
     */
    @Transactional
    fun saveRecipeJsonIngredients(json: JsonNode, recipe: Recipe) {
        json.get("extendedIngredients").forEach { ingredient ->
            val parsedIngredient = Ingredient(
                    aisle = jsoupService.escapeUserText(trimToMaxCharNum(ingredient.get("aisle").asText(), 255)),
                    consistency = jsoupService.escapeUserText(trimToMaxCharNum(ingredient.get("consistency").asText(), 255)),
                    meta = jsoupService.escapeUserText(ingredient.get("meta").asText()),
                    name = jsoupService.escapeUserText(trimToMaxCharNum(ingredient.get("name").asText(), 255)),
                    unit = jsoupService.escapeUserText(ingredient.get("unit").asText())
            )

            val savedIngredient = saveIngredient(parsedIngredient)
            val recipeIngredients = RecipeIngredients(
                    amount = ingredient.get("amount").asDouble(),
                    ingredient = savedIngredient,
                    summary = jsoupService.escapeUserText(ingredient.get("original").asText()),
                    recipe = recipe
            )
            recipe.recipeIngredients.add(saveRecipeIngredients(recipeIngredients))
        }
    }

    private fun trimToMaxCharNum(string: String, maxChars: Int): String {
        return if (string.length > maxChars) {
            string.substring(0, maxChars + 1)
        } else {
            string
        }
    }

    /**
     * Comma Separates the given "summaries" object into a string of ids excluding the "exclude" recipe list
     * @param summaries RecipeSummaries to split into id string comma separated
     * @param exclude exclude list of recipes that will not be included if summaries contains them
     * @return RecipeSummaries list returns a string of ids "132, 542, 556, 667"
     */
    private fun getCommaSeparatedIds(summaries: MutableList<RecipeSummary>, exclude: MutableList<Recipe>): String {
        val excludedIds = exclude.map { recipe ->
            recipe.spoonacularId
        }
        var commaSeparatedIds = ""
        summaries.forEachIndexed { index, summary ->
            if (!excludedIds.contains(summary.id)) {
                commaSeparatedIds += summary.id
                if (index < summaries.count() - 1) {
                    commaSeparatedIds += ","
                }
            }
        }
        return commaSeparatedIds
    }

    @Throws(UsernameNotFoundException::class, Exception::class)
    @Transactional
    fun createRecipeFromForm(recipeForm: RecipeForm, principal: Principal): Recipe {
        val user = userService.findByUsername(principal.name)
        val file = fileService.trySaveMultipartFile(recipeForm.file)
        try {
            val recipe = Recipe(
                    user = user,
                    picture = file,
                    title = jsoupService.escapeUserText(recipeForm.title!!),
                    servings = recipeForm.servings!!,
                    dairyFree = recipeForm.dairyFree!!,
                    summary = jsoupService.escapeUserText(recipeForm.summary!!),
                    instructions = jsoupService.escapeUserText(recipeForm.instructions!!),
                    sustainable = recipeForm.sustainable!!,
                    veryHealthy = recipeForm.veryHealthy!!,
                    vegetarian = recipeForm.vegetarian!!,
                    glutenFree = recipeForm.glutenFree!!,
                    pricePerServing = recipeForm.pricePerServing!!,
                    preparationMinutes = recipeForm.preparationMinutes!!,
                    readyInMinutes = recipeForm.readyInMinutes!!,
                    vegan = recipeForm.vegan!!,
                    cheap = recipeForm.cheap!!)
            val savedRecipe = recipeRepository.save(recipe)
            recipeForm.ingredientsName!!.forEachIndexed { index: Int, name: String? ->
                val ingredient = Ingredient(
                        aisle = recipeForm.ingredientsAisle!![index]?.let { jsoupService.escapeUserText(trimToMaxCharNum(it, 255)) },
                        consistency = recipeForm.ingredientsConsistency!![index]?.let { jsoupService.escapeUserText(trimToMaxCharNum(it, 255)) },
                        meta = recipeForm.ingredientsMeta!![index],
                        name = jsoupService.escapeUserText(trimToMaxCharNum(name!!, 255)),
                        unit = recipeForm.ingredientsUnit!![index]?.let { jsoupService.escapeUserText(trimToMaxCharNum(it, 255)) }!!
                )

                val savedIngredient = saveIngredient(ingredient)
                val recipeIngredients = RecipeIngredients(
                        amount = recipeForm.ingredientsAmount!![index]!!,
                        ingredient = savedIngredient,
                        summary = recipeForm.ingredientsSummary!![index]?.let { jsoupService.escapeUserText(it) },
                        recipe = savedRecipe
                )
                recipe.recipeIngredients.add(saveRecipeIngredients(recipeIngredients))
            }
            return savedRecipe
        } catch (e: Exception) {
            throw Exception("CreateRecipeException: tried to unwrap <!!> value that should be caught in validation. Are recipe values validated?")
        }
    }

    fun getComments(recipe: Recipe): MutableList<UserRecipeComment> {
        return userRecipeCommentRepository.findAllCommentsForRecipe(recipe.id)
    }

    fun getRecipesById(recipeIds: MutableList<Long>): MutableList<Recipe> {
        return recipeRepository.findAllById(recipeIds)
    }

    fun getRecipeById(recipeId: Long): Optional<Recipe> {
        return recipeRepository.findById(recipeId)
    }
}