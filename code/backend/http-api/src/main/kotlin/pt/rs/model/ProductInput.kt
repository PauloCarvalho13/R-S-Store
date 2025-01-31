package pt.rs.model

import pt.rs.Region

data class ProductInput(
    val name: String,
    val description: String,
    val price: Double,
    val region: Region,
    val listOfImagesUrls: List<String>
)
