package pt.rs.impl

import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory.getDefaultInstance
import com.google.auth.oauth2.GoogleCredentials
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.auth.http.HttpCredentialsAdapter
import jakarta.inject.Named
import org.springframework.core.io.ClassPathResource
import org.springframework.web.multipart.MultipartFile
import pt.rs.ImageService

const val GOOGLE_API_FOLDER_ID = "1hne_SajtdkfAkb7SujZ_-nsB7wd2JSjw"

@Named
class GoogleDriveService: ImageService {

    private fun getDriveService(): Drive {
        val credentialsFile = ClassPathResource("google-drive-credentials.json").inputStream

        val credentials = GoogleCredentials.fromStream(credentialsFile)
            .createScoped(listOf(DriveScopes.DRIVE_FILE))

        return Drive.Builder(
            NetHttpTransport(),
            getDefaultInstance(),
            HttpCredentialsAdapter(credentials)
        ).setApplicationName("ImageUploader").build()
    }

    override fun uploadImage(file: MultipartFile): Map<String,String> {
        val driveService = getDriveService()

        // Create file metadata
        val fileMetadata = File().apply {
            name = file.originalFilename
            parents = listOf(GOOGLE_API_FOLDER_ID) // Folder where the file will be uploaded
        }

        // Create file content from the multipart file
        val fileContent = InputStreamContent(
            file.contentType,
            file.inputStream
        )

        // Upload the file to Google Drive
        val uploadedFile = driveService.files()
            .create(fileMetadata, fileContent)
            .setFields("id, webViewLink")
            .execute()

        val res = mapOf(
            "id" to uploadedFile.id,
            "url" to uploadedFile.webViewLink
        )

        return res.ifEmpty { throw Exception("Error uploading file") }

    }

    override fun deleteImage(imageId: String) {
        val driveService = getDriveService()
        driveService.files().delete(imageId).execute()
    }
}