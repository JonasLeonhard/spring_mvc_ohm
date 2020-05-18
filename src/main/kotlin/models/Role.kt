package models

import javax.persistence.*

@Entity
@Table(name = "role")
data class Role(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1,

        @Column(length = 50, unique = true)
        var name: String = "USER"
)