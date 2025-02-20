package pt.rs.model

import pt.rs.ImageDetails
import pt.rs.Region

data class SellerProductInfo(
    val name: String,
    val description: String,
    val price: Double,
    val region: Region,
    val imagesUrls: List<ImageDetails>
)
