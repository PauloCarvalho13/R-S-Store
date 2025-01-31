package pt.rs.user

class AuthenticatedUser(
    val user: User,
    val token: String
)