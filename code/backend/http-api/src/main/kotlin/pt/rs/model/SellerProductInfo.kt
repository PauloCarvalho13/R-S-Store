package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema
import pt.rs.ImageDetails
import pt.rs.Region

@Schema(description = "Information about a product that a seller sees")
data class SellerProductInfo(
    @field:Schema(description = "Product name", example = "Product name")
    val name: String,
    @field:Schema(description = "Product description", example = "Product description")
    val description: String,
    @field:Schema(description = "Product price", example = "10.0")
    val price: Double,
    @field:Schema(description = "Product region", example = "NORTH")
    val region: Region,
    @field:Schema(description = "Product images", example = "[{\"id\":\"1\",\"url\":\"http://url.com\"}]")
    val imagesUrls: List<ImageDetails>
)
