package pt.rs.model

import pt.rs.Announcer
import pt.rs.Region

data class ProductDetails(
    val announcer: Announcer,
    val name: String,
    val description: String,
    val price: Double,
    val region: Region,
    val listOfImagesUrls: List<String>
)
