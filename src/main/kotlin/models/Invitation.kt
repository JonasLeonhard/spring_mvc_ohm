package models

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
data class Invitation(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = -1,

        @OneToOne(targetEntity = Recipe::class)
        val recipe: Recipe,

        @OneToOne(targetEntity = User::class)
        val user: User,

        @OneToMany(targetEntity = User::class)
        val friends: MutableList<User>,

        @Temporal(TemporalType.DATE)
        val date: Date,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var updatedAt: Date = Date.from(Instant.now())
)