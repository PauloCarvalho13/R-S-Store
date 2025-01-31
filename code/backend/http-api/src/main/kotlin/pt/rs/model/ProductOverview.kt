package pt.rs.model

import pt.rs.Region

data class ProductOverview(
    val id: Int,
    val name: String,
    val price: Double,
    val region: Region,
    val imageUrl: String
)
