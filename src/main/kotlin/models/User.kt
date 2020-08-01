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
        @OneToMany(targetEntity = Recipe::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        @JoinColumn(name = "user_id")
        var recipes: MutableSet<Recipe> = mutableSetOf(),

        @OneToMany(targetEntity = UserLikedRecipe::class, mappedBy = "embeddedKey.user")
        @LazyCollection(LazyCollectionOption.FALSE)
        var likedRecipes: MutableSet<UserLikedRecipe> = mutableSetOf(),

        @OneToMany(targetEntity = Friendship::class, mappedBy = "requested_by")
        var friendshipsFromThisUser: MutableSet<Friendship> = mutableSetOf(),

        @OneToMany(targetEntity = Friendship::class, mappedBy = "request_to")
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
}