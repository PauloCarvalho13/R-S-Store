package pt.rs.model

import pt.rs.ImageDetails
import pt.rs.Region

data class ProductOverview(
    val id: Int,
    val name: String,
    val price: Double,
    val region: Region,
    val image: ImageDetails
)
