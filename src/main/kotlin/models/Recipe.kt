package models

import org.springframework.lang.Nullable
import javax.persistence.*

@Entity
@Table(name = "recipe")
class Recipe(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @field:Nullable
        @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var user: User? = null,

        var title: String,

        @OneToOne(targetEntity = File::class, cascade = [CascadeType.ALL])
        @field:Nullable
        var picture: File? = null,

        var servings: Int,

        var readyInMinutes: Int,

        var likes: Int = 0,

        var pricePerServing: Double = 0.0,

        var glutenFree: Boolean = false,

        var vegan: Boolean = false,

        var vegetarian: Boolean = false,

        var instructions: String,

        var summary: String,

        @OneToMany(targetEntity = Recipe_Ingredients::class, fetch = FetchType.EAGER, mappedBy = "recipe")
        var recipe_ingredients: MutableList<Recipe_Ingredients> = mutableListOf(),

        var spoonacularId: Long?
)
