package models

import javax.persistence.*

@Entity
@Table(name = "role")
data class Role(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1,

        @Column(unique = true)
        var name: String = "",

        @ManyToMany(mappedBy = "roles")
        val users: Set<User> = setOf<User>()
)