package pt.rs.model

import pt.rs.Region
import pt.rs.Announcer

data class ProductUpdateInput(
    val announcer: Announcer,
    val name: String,
    val description: String,
    val price: Double,
    val region: Region,
    val listOfImagesUrls: List<String>
)
