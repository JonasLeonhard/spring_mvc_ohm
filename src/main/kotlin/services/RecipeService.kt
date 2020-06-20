package services

import configurations.ApplicationPropertiesConfiguration
import models.Recipe
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class RecipeService(val props: ApplicationPropertiesConfiguration) {
    val spoonacularWebClient: WebClient = WebClient.builder()
            .baseUrl("https://api.spoonacular.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

    fun searchApi(q: String): MutableList<Recipe> {
        // creat restTemplate Bean
        // check if already searched for by spoonacular api
        // if not: search in api and add to database
        val response = spoonacularWebClient.get().uri { builder ->
            builder.path("requestedPath")
                    .queryParam("apiKey", props.ENV_SPOONACULAR_API_KEY)
                    .queryParam("q", q)
                    .build()
        }.retrieve().bodyToMono(Recipe::class.java).subscribe { recipe ->
            println("recipe : $recipe")
        }

        //val unwrapped = response ?: "not found!"
        //println("got res:::$unwrapped")
        return TODO()
    }

}