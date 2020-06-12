package models

import org.springframework.lang.Nullable
import javax.persistence.*

@Entity
@Table(name = "recipe")
class Recipe(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1,

        @field:Nullable
        @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
        var user: User? = null,

        var title: String,

        var total_time: Int,

        var yields: Int,

        @OneToMany(targetEntity = Ingredient::class, fetch = FetchType.EAGER)
        @JoinColumn(name = "recipe_id")
        var ingredients: MutableList<Ingredient>

)
