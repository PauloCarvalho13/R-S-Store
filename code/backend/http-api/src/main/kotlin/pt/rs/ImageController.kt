package pt.rs

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pt.rs.impl.GoogleDriveService
import pt.rs.user.AuthenticatedUser

@Suppress("unused")
@RestController
@RequestMapping("/api/images")
class ImageController(
    private val googleDriveService: GoogleDriveService
) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("image") file: MultipartFile, authUser: AuthenticatedUser): ResponseEntity<Map<String, String>> {
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

    @DeleteMapping("/{imageId}")
    fun deleteFile(@PathVariable imageId: String, authUser: AuthenticatedUser): ResponseEntity<Map<String, String>> {
        return try {
            googleDriveService.deleteImage(imageId)
            ResponseEntity.ok(mapOf("message" to "File deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message!!))
        }
    }
}
