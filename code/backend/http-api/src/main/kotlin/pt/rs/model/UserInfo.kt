package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User information")
data class UserInfo(
    @field:Schema(description = "User ID", example = "1", required = true)
    val id: Int,
    @field:Schema(description = "User name", example = "John Doe", required = true)
    val name: String,
    @field:Schema(description = "User email", example = "johndoe@email.com", required = true)
    val email: String
)
