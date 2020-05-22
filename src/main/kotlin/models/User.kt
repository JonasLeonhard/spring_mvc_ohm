package models

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
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

        @ManyToMany(targetEntity = Role::class, fetch = FetchType.EAGER)
        var roles: MutableSet<Role> = mutableSetOf(),

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        private var createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        private var updatedAt: Date = Date.from(Instant.now())
)