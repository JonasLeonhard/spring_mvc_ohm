package models

import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_profile_buy_list")
class UserRecipeBuyList(
        user: User,
        recipe: Recipe,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now()),

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var buyAtDate: Date = Date.from(Instant.now())
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
    private var embeddedKey: UserRecipeBuyListCompositeKey = UserRecipeBuyListCompositeKey(user, recipe)

    fun isSameRow(userLikedRecipe: UserRecipeBuyList): Boolean {
        return this.user.id == userLikedRecipe.user.id && this.recipe.id == userLikedRecipe.recipe.id
    }
}

@Embeddable
data class UserRecipeBuyListCompositeKey(
        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne
        @JoinColumn(name = "recipe_id")
        var recipe: Recipe
) : Serializable