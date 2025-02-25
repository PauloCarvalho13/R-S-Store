package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema
import pt.rs.Announcer
import pt.rs.ImageDetails
import pt.rs.Region

@Schema(description = "Product details")
data class ProductDetails(
    @field:Schema(description = "Announcer of the product", implementation = Announcer::class)
    val announcer: Announcer,
    @field:Schema(description = "Product id", example = "1")
    val id: Int,
    @field:Schema(description = "Product name", example = "Product name")
    val name: String,
    @field:Schema(description = "Product description", example = "Product description")
    val description: String,
    @field:Schema(description = "Product price", minimum = "0.0", example = "10.0")
    val price: Double,
    @field:Schema(description = "Product region", implementation = Region::class)
    val region: Region,
    @field:Schema(description = "List of images details")
    val imagesDetails: List<ImageDetails>
)
