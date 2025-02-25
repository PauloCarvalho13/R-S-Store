package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema
import pt.rs.ImageDetails
import pt.rs.Region

@Schema(description = "Product overview")
data class ProductOverview(
    @field:Schema(description = "Product id", example = "1")
    val id: Int,
    @field:Schema(description = "Product name", example = "Product name")
    val name: String,
    @field:Schema(description = "Product price", example = "10.0")
    val price: Double,
    @field:Schema(description = "Product region", example = "NORTH")
    val region: Region,
    @field:Schema(description = "Product main image", example = "{\"id\":\"1\",\"url\":\"http://url.com\"}")
    val image: ImageDetails
)
