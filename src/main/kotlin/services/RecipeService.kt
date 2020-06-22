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
import pojos.SpoonacularRecipeSummary
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
    fun searchApi(q: String): MutableList<Recipe> {
        val recipeSummaries = getRecipeSummariesSpoonacular(q)
        // add database recipes that fit query params by adding them to the list?


        // TODO -> seperate this into clicked a sum get recipe by id:
        // create a coroutine -> check if sums are indexed, if not: save them
        val recipeFromSum = this.getIndexedRecipe(recipeSummaries[0])
        println("get recipeFromSummaries 0 : $recipeFromSum")
        val recievedRecipeFromSum = this.saveUnindexedRecipe(recipeSummaries[0])
        // foreach recipe summary:
        // check if it is indexed
        // if yes: get indexed
        // if not save it
        return TODO()
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

    @Transactional
    fun saveIngredients(ingredients: MutableList<Ingredient>): List<Ingredient> {
        return ingredients.map { ingredient ->
            saveIngredient(ingredient)
        }
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
        return this.recipeRepository.getIndexedRecipe(recipeSummary)
    }

    @Transactional
    fun saveUnindexedRecipe(recipeSummary: SpoonacularRecipeSummary): Recipe {
        val json = spoonacularWebClient.get().uri { builder ->
            builder.path("/recipes/${recipeSummary.id}/information")
                    .queryParam("apiKey", props.ENV_SPOONACULAR_API_KEY)
                    .queryParam("includeNutrition", true)
                    .build()
        }.retrieve().bodyToMono<JsonNode>(JsonNode::class.java).block()

        println("response : $json")
        if (json != null) {
            val parsedRecipe = Recipe(
                    instructions = json.get("instructions").asText(),
                    readyInMinutes = json.get("readyInMinutes").asInt(),
                    servings = json.get("servings").asInt(),
                    spoonacularId = json.get("id").asLong(),
                    summary = trimToMaxCharNum(json.get("summary").asText(), 255),
                    title = json.get("title").asText()
            )
            val savedRecipe = saveRecipe(parsedRecipe)

            json.get("extendedIngredients").forEach { ingredient ->
                println("parse ingredient: $ingredient")
                val parsedIngredient = Ingredient(
                        aisle = trimToMaxCharNum(ingredient.get("aisle").asText(), 255),
                        consistency = trimToMaxCharNum(ingredient.get("consistency").asText(), 255),
                        meta = trimToMaxCharNum(ingredient.get("meta").asText(), 255),
                        name = trimToMaxCharNum(ingredient.get("name").asText(), 255),
                        summary = trimToMaxCharNum(ingredient.get("original").asText(), 255),
                        unit = ingredient.get("unit").asText()
                )
                println("parsed ingredient $parsedIngredient")
                val savedIngredient = saveIngredient(parsedIngredient)
                println("saved ingredient ...")
                val recipeIngredients = RecipeIngredients(
                        amount = ingredient.get("amount").asDouble(),
                        ingredient = savedIngredient,
                        recipe = savedRecipe
                )
                saveRecipeIngredients(recipeIngredients)


                println("added recipe_ingredients for ingredient...")
            }
            println("after loop!")
            //println("got recipe $parsedRecipe")
            //println("got ingredients $ingredients")
            //println("got recipeIngredients $recipeIngredients")
            // save all ingredients
            //val saved = saveIngredients(ingredients)


            println("saved Ingredients $savedRecipe")
            // save the recipe
        }
        // this retrieves the full recipe from the spoonacular api and chaches it in the database
        TODO()
    }

    private fun trimToMaxCharNum(string: String, maxChars: Int): String {
        return if (string.length > maxChars) {
            string.substring(0, maxChars + 1)
        } else {
            string
        }
    }
}