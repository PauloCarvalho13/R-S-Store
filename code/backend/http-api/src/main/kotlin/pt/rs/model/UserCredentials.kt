package pt.rs.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Input for login in with an already registered user")
data class UserCredentials(
    @field:Schema(description = "User email", example = "johndoe@email.com", format = "email", required = true)
    val email: String,
    @field:Schema(description = "User password", example = "password", required = true)
    val password: String
)
