package models
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import org.springframework.lang.Nullable
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "recipe")
data class Recipe(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @field:Nullable
        @ManyToOne(targetEntity = User::class, fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinColumn(name = "user_id")
        var user: User? = null,

        var title: String,

        @OneToOne(targetEntity = File::class, cascade = [CascadeType.ALL])
        @field:Nullable
        var picture: File? = null,

        var servings: Int,

        var readyInMinutes: Int,

        var preparationMinutes: Int,

        var likes: Int = 0,

        @OneToMany(targetEntity = UserLikedRecipe::class, mappedBy = "embeddedKey.recipe")
        var userLikes: MutableSet<UserLikedRecipe> = mutableSetOf(),

        var pricePerServing: Double = 0.0,

        var glutenFree: Boolean = false,

        var vegan: Boolean = false,

        var vegetarian: Boolean = false,

        var dairyFree: Boolean = false,

        var veryHealthy: Boolean = false,

        var cheap: Boolean = false,

        var veryPopular: Boolean = false,

        var sustainable: Boolean = false,

        @Column(columnDefinition = "TEXT")
        var instructions: String,

        @Column(columnDefinition = "TEXT")
        var summary: String,

        @OneToMany(targetEntity = RecipeIngredients::class, mappedBy = "embeddedKey.recipe")
        @LazyCollection(LazyCollectionOption.FALSE)
        var recipeIngredients: MutableList<RecipeIngredients> = mutableListOf(),

        var spoonacularId: Long? = null,

        var recipeImageUrl: String? = null,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now())
)
