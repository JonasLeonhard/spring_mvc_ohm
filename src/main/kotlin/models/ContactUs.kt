package models

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@Table(name = "contactUs")
data class ContactUs(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @field:NotBlank(message = "Firstname cannot be empty")
        @field:NotNull
        var firstName: String? = null,

        @field:NotBlank(message = "Firstname cannot be empty")
        @field:NotNull
        var lastName: String? = null,

        @field:Email(message = "Email not correct")
        @field:NotNull
        var email: String? = null,

        @Column(length = 50000)
        @field:NotNull
        @field:NotBlank(message = "Message cannot be empty")
        var message: String? = null,

        var AdminChecked: Boolean = false,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now()),

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var updatedAt: Date = Date.from(Instant.now())
)