package pt.rs

data class ImageDetails (
    val id: String,
    val uri: String
){
    init {
        require(id.isNotBlank()) { "Image id must not be blank" }
        require(uri.isNotBlank()) { "Image uri must not be blank" }
    }
}