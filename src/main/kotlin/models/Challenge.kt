package models

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*


@Entity
data class Challenge(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @OneToOne
        var recipe: Recipe,

        @OneToMany(targetEntity = UserChallenge::class, mappedBy = "embeddedKey.challenge")
        var userChallenges: MutableList<UserChallenge> = mutableListOf(),

        var dayChallenge: Boolean = false,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var updatedAt: Date = Date.from(Instant.now())
)
