package pojos

import com.sun.istack.Nullable
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AddIngredientForm(
        @field:NotBlank(message = "Name cannot be empty")
        @field:NotNull(message = "Name has to be specified")
        val name: String? = null,

        @Nullable
        var file: MultipartFile? = null,

        @field:NotNull(message = "Aisle has to be specified")
        @field:NotBlank(message = "Aisle cannot be empty")
        var aisle: String? = null,

        @field:NotNull(message = "Consistency has to be specified")
        @field:NotBlank(message = "Consistency cannot be empty")
        var consistency: String? = null,

        @Nullable
        var meta: String? = null,

        @field:NotNull(message = "Unit has to be specified")
        @field:NotBlank(message = "Unit cannot be empty")
        var unit: String? = null
)