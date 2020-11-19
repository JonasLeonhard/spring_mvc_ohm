package models

import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
data class Search(
        @Id
        @Column(unique = true)
        val query: String,

        var searchAmount: Long = 1,

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var updatedAt: Date = Date.from(Instant.now())
)