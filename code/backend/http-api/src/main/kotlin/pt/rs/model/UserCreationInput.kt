package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Input for creating a new user")
data class UserCreationInput(
    @field:Schema(description = "User name", example = "John Doe", required = true)
    val name: String,
    @field:Schema(description = "User email", example = "johndoe@email.com", required = true)
    val email: String,
    @field:Schema(description = "User password", example = "password", required = true)
    val password: String
)
