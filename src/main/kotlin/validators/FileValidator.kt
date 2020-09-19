package validators

import org.springframework.validation.Errors
import org.springframework.web.multipart.MultipartFile

class FileValidator {
    companion object Static {
        /**
         * Adds errors.rejectValue(field, "InvalidMimeTypeException", "...")
         * */
        fun fileMimeTypeValid(errors: Errors, file: MultipartFile?, field: String) {
            if (
                    file != null &&
                    !file.isEmpty &&
                    !(file.contentType?.toLowerCase().equals("image/jpg")
                            || file.contentType?.toLowerCase().equals("image/jpeg")
                            || file.contentType?.toLowerCase().equals("image/png"))) {
                errors.rejectValue(field, "InvalidMimeTypeException", "jpg, jpeg & png file types are only supported")
            }
        }
    }
}