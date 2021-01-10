package pojos

import com.sun.istack.Nullable
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ChallengeUploadForm(
        @field:NotNull(message = "Challenge experience has to be specified")
        @field:NotBlank(message = "Challenge experience cannot be empty")
        val experience: String? = null,

        @Nullable
        var file: MultipartFile? = null
)