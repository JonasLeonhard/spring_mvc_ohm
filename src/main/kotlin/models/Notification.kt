package models

import org.springframework.lang.NonNull
import javax.persistence.*

@Entity
data class Notification(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1,

        @field:NonNull
        @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var user: User
)