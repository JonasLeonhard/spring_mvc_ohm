package models

import org.springframework.lang.Nullable
import javax.persistence.*

@Entity
data class Ingredient(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @OneToMany(targetEntity = Recipe_Ingredients::class, fetch = FetchType.EAGER, mappedBy = "ingredient")
        var recipe_ingredients: MutableList<Recipe_Ingredients> = mutableListOf(),

        var name: String,

        var summary: String?,

        var meta: String?,

        var aisle: String?,

        var consistency: String?,

        var unit: String,

        @OneToOne(targetEntity = File::class, cascade = [CascadeType.ALL])
        @field:Nullable
        var picture: File? = null
)