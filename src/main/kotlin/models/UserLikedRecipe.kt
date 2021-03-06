package models

import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_profile_liked_recipe")
class UserLikedRecipe(
        user: User,
        recipe: Recipe,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now())
) {
    var user: User
        get() = this.embeddedKey.user
        set(value) {
            this.embeddedKey.user = value
        }

    var recipe: Recipe
        get() = this.embeddedKey.recipe
        set(value) {
            this.embeddedKey.recipe = value
        }

    @EmbeddedId
    private var embeddedKey: UserLikedRecipeCompositeKey = UserLikedRecipeCompositeKey(user, recipe)

    fun isSameRow(userLikedRecipe: UserLikedRecipe): Boolean {
        return this.user.id == userLikedRecipe.user.id && this.recipe.id == userLikedRecipe.recipe.id
    }
}

@Embeddable
data class UserLikedRecipeCompositeKey(
        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne
        @JoinColumn(name = "recipe_id")
        var recipe: Recipe
) : Serializable