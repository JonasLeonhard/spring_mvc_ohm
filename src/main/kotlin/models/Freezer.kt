package models

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "user_profile_freezer")
class Freezer(
        user: User,
        ingredient: Ingredient,
        var amount: Int
) {
    var user: User
        get() = this.embeddedKey.user
        set(value) {
            this.embeddedKey.user = value
        }

    var ingredient: Ingredient
        get() = this.embeddedKey.ingredient
        set(value) {
            this.embeddedKey.ingredient = value
        }

    @EmbeddedId
    private var embeddedKey: FreezerCompositeKey = FreezerCompositeKey(user, ingredient)
}

@Embeddable
data class FreezerCompositeKey(
        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne
        @JoinColumn(name = "ingredient_id")
        var ingredient: Ingredient
) : Serializable