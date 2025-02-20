package pt.rs.model

import pt.rs.Announcer
import pt.rs.ImageDetails
import pt.rs.Region

data class ProductDetails(
    val announcer: Announcer,
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val region: Region,
    val imagesDetails: List<ImageDetails>
)
