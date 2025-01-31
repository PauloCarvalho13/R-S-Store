package pt.rs.model

import pt.rs.Region

data class ProductInputQuery(
    val region: Region?,
    val minPrice: Double?,
    val maxPrice: Double?
)
