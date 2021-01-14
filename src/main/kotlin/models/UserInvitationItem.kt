package models

import javax.persistence.*

@Entity
data class UserInvitationItem(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = -1,

        @OneToOne
        val invitation: Invitation,

        @OneToOne
        val user: User,

        val item: String? = null,

        @OneToOne(targetEntity = RecipeIngredients::class)
        val recipeIngredient: RecipeIngredients? = null
)
