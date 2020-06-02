package models

import com.sun.istack.Nullable
import org.hibernate.annotations.CreationTimestamp
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
        @GeneratedValue(strategy = GenerationType.AUTO)
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

        @OneToMany(targetEntity = Friendship::class, mappedBy = "requested_by")
        var friendshipsFromUsers: MutableSet<Friendship> = mutableSetOf(),

        @OneToMany(targetEntity = Friendship::class, mappedBy = "request_to")
        var friendshipsToUsers: MutableSet<Friendship> = mutableSetOf(),

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var updatedAt: Date = Date.from(Instant.now())
)