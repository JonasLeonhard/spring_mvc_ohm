package models

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.MapsId

@Entity
data class Recipe_Ingredients(
        @Id
        var id: Long = -1,

        @ManyToOne
        @MapsId
        var recipe: Recipe,

        @ManyToOne
        @MapsId
        var ingredient: Ingredient,

        var amount: Int
)