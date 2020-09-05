package models

import com.sun.istack.Nullable
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "user_profile")
data class User(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @Column(unique = true)
        @field:NotNull
        @field:Size(min = 5, max = 50)
        var username: String = "",

        @field:NotNull
        @field:Size(min = 8, message = "a password must contain at least 8 characters")
        var password: String = "",

        @Transient
        var passwordConfirm: String = "",

        var name: String = "",

        var enabled: Boolean = true,
        var accountNonExpired: Boolean = true,
        var accountNonLocked: Boolean = true,
        var credentialsNonExpired: Boolean = true,

        @Transient
        @Nullable
        var file: MultipartFile? = null,

        @OneToOne(targetEntity = File::class, cascade = [CascadeType.ALL])
        @field:Nullable
        var picture: File? = null,

        @ManyToMany(targetEntity = Role::class, fetch = FetchType.EAGER)
        var roles: MutableSet<Role> = mutableSetOf(),

        @field:Nullable
        @OneToMany(targetEntity = Recipe::class, fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        var recipes: MutableSet<Recipe> = mutableSetOf(),

        @OneToMany(targetEntity = UserLikedRecipe::class, mappedBy = "embeddedKey.user")
        @LazyCollection(LazyCollectionOption.FALSE)
        var likedRecipes: MutableSet<UserLikedRecipe> = mutableSetOf(),

        @OneToMany(targetEntity = Recipe::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var favoriteRecipes: MutableSet<Recipe> = mutableSetOf(),

        @OneToMany(targetEntity = UserRecipeBuyList::class, mappedBy = "embeddedKey.user")
        @LazyCollection(LazyCollectionOption.FALSE)
        var buyList: MutableSet<UserRecipeBuyList> = mutableSetOf(),

        @OneToMany(targetEntity = Friendship::class, mappedBy = "requested_by", cascade = [CascadeType.ALL])
        var friendshipsFromThisUser: MutableSet<Friendship> = mutableSetOf(),

        @OneToMany(targetEntity = Friendship::class, mappedBy = "request_to", cascade = [CascadeType.ALL])
        var friendshipsToThisUser: MutableSet<Friendship> = mutableSetOf(),

        @OneToMany(targetEntity = Notification::class)
        var notifications: MutableSet<Notification> = mutableSetOf(),

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var updatedAt: Date = Date.from(Instant.now())
) {
        /**
         * Returns whether or not the user has liked the given recipe
         * */
        fun hasLikedRecipe(recipe: Recipe): Boolean {
                recipe.userLikes.forEach { recipeLikeRow ->
                        if (recipeLikeRow.user.id == this.id) {
                                return true
                        }
                }

                return false
        }

        fun hasFavoritedRecipe(recipe: Recipe): Boolean {
                return this.favoriteRecipes.contains(recipe)
        }

        /**
         * @return boolean if the recipe is on this users buylist
         */
        fun hasBuyListedRecipe(recipe: Recipe): Boolean {
                this.buyList.forEach { userRecipeBuyList ->
                        if (userRecipeBuyList.recipe.id == recipe.id) {
                                return true
                        }
                }
                return false
        }
}