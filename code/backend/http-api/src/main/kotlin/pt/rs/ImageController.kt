package pt.rs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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

    @Operation(summary = "Upload an image")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Image uploaded"),
        ApiResponse(responseCode = "400", description = "No file uploaded"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @PostMapping("/upload", consumes = ["multipart/form-data"])
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

    @Operation(summary = "Delete an image")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Image deleted"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @DeleteMapping("/{imageId}", produces = ["application/json"])
    fun deleteFile(@PathVariable imageId: String, authUser: AuthenticatedUser): ResponseEntity<Map<String, String>> {
        return try {
            googleDriveService.deleteImage(imageId)
            ResponseEntity.ok(mapOf("message" to "File deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message!!))
        }
    }
}
