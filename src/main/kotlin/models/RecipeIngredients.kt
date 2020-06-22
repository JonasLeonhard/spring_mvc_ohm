package models

import java.io.Serializable
import javax.persistence.*

@Entity
class RecipeIngredients(
        recipe: Recipe,
        ingredient: Ingredient,
        var amount: Double
) {
        var recipe: Recipe
                get() = this.embeddedKey.recipe
                set(value) {
                        this.embeddedKey.recipe = value
                }

        var ingredient: Ingredient
                get() = this.embeddedKey.ingredient
                set(value) {
                        this.embeddedKey.ingredient = value
                }

        @EmbeddedId
        private var embeddedKey: RecipeIngredientsCompositeKey = RecipeIngredientsCompositeKey(recipe, ingredient)
}

@Embeddable
data class RecipeIngredientsCompositeKey(
        @ManyToOne
        @JoinColumn(name = "recipe_id")
        var recipe: Recipe,

        @ManyToOne
        @JoinColumn(name = "ingredient_id")
        var ingredient: Ingredient
) : Serializable
