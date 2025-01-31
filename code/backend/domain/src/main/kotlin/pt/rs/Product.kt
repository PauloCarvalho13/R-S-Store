package pt.rs

data class Product (
    val announcer: Announcer,
    val id: Int,
    val name: String,
    val description: String,
    val region: Region,
    val price: Double,
    val listOfImagesUrls: List<String>
){
    init {
        require(price > 0.00) { "Invalid price" }
        require(name.isNotBlank()) { "Invalid product name" }
    }
}
