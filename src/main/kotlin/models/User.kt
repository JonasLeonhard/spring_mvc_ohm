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
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        var username: String,

        val password: String,

        @Transient
        val passwordConfirm: String,

        val name: String = "defaultName",

        @ManyToMany
        val roles: Set<Role>,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        private val createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        private val updatedAt: Date = Date.from(Instant.now())
)