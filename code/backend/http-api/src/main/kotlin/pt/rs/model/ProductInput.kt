package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema
import pt.rs.ImageDetails
import pt.rs.Region

@Schema(description = "Product input")
data class ProductInput(
    @field:Schema(description = "Product name", example = "Product name", required = true)
    val name: String,
    @field:Schema(description = "Product description", example = "Product description", required = true)
    val description: String,
    @field:Schema(description = "Product price", minimum = "0.0", required = true, example = "10.0")
    val price: Double,
    @field:Schema(description = "Product region", implementation = Region::class, required = true)
    val region: Region,
    @field:Schema(description = "List of images details", required = true)
    val listOfImagesUrls: List<ImageDetails>
)
