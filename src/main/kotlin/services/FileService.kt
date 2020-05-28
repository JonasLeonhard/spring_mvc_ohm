package services

import models.File
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import repositories.FileRepository
import java.util.*

@Service
class FileService(val fileRepository: FileRepository) {

    fun trySaveMultipartFile(file: MultipartFile?): File? {
        if (file != null && !(file!!.isEmpty)) {
            val fileName = file!!.originalFilename ?: "undefined"
            val mimeType = file!!.contentType ?: "undefined"
            val bytes = file!!.bytes
            try {
                val saveFile = File(fileName = fileName, mimeType = mimeType, bytes = bytes)
                return fileRepository.save(saveFile)
            } catch (exc: Exception) {
                throw FileSaveException("Could not store file ${fileName}:${exc.message}")
            }
        }
        return null
    }

    fun getFile(id: Long): Optional<File> {
        return fileRepository.findById(id)
    }
}

class FileSaveException(override val message: String?) : Exception(message)