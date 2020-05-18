package models

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_profile")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1,

        @Column(unique = true)
        var username: String = "",

        var password: String = "",

        @Transient
        var passwordConfirm: String = "",

        var name: String = "",

        @ManyToMany(targetEntity = Role::class, fetch = FetchType.EAGER)
        var roles: MutableSet<Role> = mutableSetOf(),

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        private var createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        private var updatedAt: Date = Date.from(Instant.now())
)