package models

import org.springframework.lang.Nullable
import javax.persistence.*

@Entity
data class Ingredient(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @field:Nullable
        @ManyToOne(targetEntity = Recipe::class, fetch = FetchType.EAGER)
        var recipe: Recipe? = null
)