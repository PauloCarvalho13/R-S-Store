package pt.rs

import org.springframework.stereotype.Component
import pt.rs.user.AuthenticatedUser

@Suppress("unused")
@Component
class RequestTokenProcessor(
    private val userService: UserService
) {
    fun processAuthHeaderVal(authValue: String?): AuthenticatedUser?{
        if (authValue == null) return null

        val parts = authValue.trim().split(" ")

        if (parts.size != 2 && parts[0] != SCHEME) return null

        return userService.getUserByToken(parts[1])?.let {
            AuthenticatedUser(
                it,
                parts[1]
            )
        }
    }

    companion object {
        const val SCHEME = "bearer"
    }
}