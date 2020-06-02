package controllers

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import services.FileService


@Controller
@RequestMapping("/resource")
class ResourceController(val fileService: FileService) {

    @GetMapping("/{file_id}", produces = [MediaType.IMAGE_PNG_VALUE])
    fun getBlobImgFromDatabase(@PathVariable("file_id") fileId: Long): ResponseEntity<ByteArray> {

        val fileContent: ByteArray = fileService.getFile(fileId).get().bytes
        val responseHeader = HttpHeaders()
        responseHeader.contentType = MediaType.IMAGE_PNG

        return ResponseEntity(fileContent, responseHeader, HttpStatus.OK)
    }
}