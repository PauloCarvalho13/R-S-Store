package pt.rs

import org.springframework.web.multipart.MultipartFile

interface ImageService {
    /**
     * Uploads an image to the storage service
     * @param file The image file to upload
     * @return The URL of the uploaded image and its ID
     */
    fun uploadImage(file: MultipartFile): Map<String, String>

    /**
     * Deletes an image from the storage service
     * @param imageId The ID of the image to delete
     */
    fun deleteImage(imageId: String)
}