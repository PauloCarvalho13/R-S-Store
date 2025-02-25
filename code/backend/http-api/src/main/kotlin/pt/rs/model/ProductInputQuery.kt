package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema
import pt.rs.Region

@Schema(description = "Query to filter products")
data class ProductInputQuery(
    @field:Schema(description = "Region to filter products", nullable = true, implementation = Region::class)
    val region: Region?,
    @field:Schema(description = "Minimum price to filter products", nullable = true, minimum = "0")
    val minPrice: Double?,
    @field:Schema(description = "Maximum price to filter products", nullable = true)
    val maxPrice: Double?
)
