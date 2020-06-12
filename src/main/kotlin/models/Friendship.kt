package models

import org.springframework.lang.NonNull
import javax.persistence.*

@Entity
@Table(name = "friendship")
class Friendship(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @field:NonNull
        @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var requested_by: User,

        @field:NonNull
        @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var request_to: User,

        var accepted: Boolean = false
)