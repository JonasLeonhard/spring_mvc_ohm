package models

import org.springframework.lang.Nullable
import javax.persistence.*

@Entity
data class Ingredient(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @OneToMany(targetEntity = RecipeIngredients::class, fetch = FetchType.EAGER, mappedBy = "embeddedKey.ingredient")
        var recipeIngredients: MutableList<RecipeIngredients> = mutableListOf(),

        @Column(unique = true)
        var name: String,

        var meta: String?,

        var aisle: String?,

        var consistency: String?,

        var unit: String,

        @OneToOne(targetEntity = File::class, cascade = [CascadeType.ALL])
        @field:Nullable
        var picture: File? = null
)