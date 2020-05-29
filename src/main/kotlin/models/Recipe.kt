package models

import org.springframework.lang.Nullable
import javax.persistence.*

@Entity
@Table(name = "recipe")
class Recipe(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = -1,

        @field:Nullable
        @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var user: User? = null
)