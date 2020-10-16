package models

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_invitation_comments")
data class UserInvitationComment(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "invitation_id")
        var invitation: Invitation,

        var message: String,

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        var createdAt: Date = Date.from(Instant.now())
)