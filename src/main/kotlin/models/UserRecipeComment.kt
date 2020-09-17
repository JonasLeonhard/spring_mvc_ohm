package models

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_recipe_comments")
data class UserRecipeComment(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "recipe_id")
        var recipe: Recipe,

        var message: String,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now())
)
//{
//        var user: User
//                get() = this.embeddedKey.user
//                set(value) {
//                        this.embeddedKey.user = value
//                }
//
//        var recipe: Recipe
//                get() = this.embeddedKey.recipe
//                set(value) {
//                        this.embeddedKey.recipe = value
//                }
//
//        @EmbeddedId
//        private var embeddedKey: UserCommentCompositeKey = UserCommentCompositeKey(user, recipe)
//}
//
//@Embeddable
//data class UserCommentCompositeKey(
//        @ManyToOne
//        @JoinColumn(name = "user_id")
//        var user: User,
//
//        @ManyToOne
//        @JoinColumn(name = "recipe_id")
//        var recipe: Recipe
//) : Serializable