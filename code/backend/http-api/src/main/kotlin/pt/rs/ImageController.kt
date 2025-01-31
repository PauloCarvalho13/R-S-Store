package pt.rs

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import pt.rs.impl.GoogleDriveService

@Suppress("unused")
@RestController
@RequestMapping("/rsStore/images")
class ImageController(
    private val googleDriveService: GoogleDriveService
) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("image") file: MultipartFile): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "No file uploaded!"))
        }
        return try {
            val idAndUri = googleDriveService.uploadImage(file)
            ResponseEntity.ok(idAndUri)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message!!))
        }
    }

    @PostMapping("/delete/{imageId}")
    fun deleteFile(@PathVariable imageId: String): ResponseEntity<Map<String, String>> {
        return try {
            googleDriveService.deleteImage(imageId)
            ResponseEntity.ok(mapOf("message" to "File deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message!!))
        }
    }
}
