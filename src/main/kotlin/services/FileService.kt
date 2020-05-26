package services

import models.File
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import repositories.FileRepository
import java.util.*

@Service
class FileService(val fileRepository: FileRepository) {

    fun saveFile(file: MultipartFile): File? {
        val fileName = file.originalFilename ?: "undefined_name"
        val mimeType = file.contentType ?: return null
        try {
            val saveFile = File(fileName = fileName, mimeType = mimeType, byte = file.bytes)
            return fileRepository.save(saveFile)
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
        return null
    }

    fun getFile(id: Long): Optional<File> {
        return fileRepository.findById(id)
    }
}