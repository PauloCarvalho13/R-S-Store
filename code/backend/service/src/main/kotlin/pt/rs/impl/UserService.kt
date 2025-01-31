package pt.rs.impl

import jakarta.inject.Named
import kotlinx.datetime.Clock
import pt.rs.UserService
import pt.rs.user.User
import kotlinx.datetime.Instant
import pt.rs.TransactionManager
import pt.rs.user.Token
import pt.rs.user.UsersDomain

sealed class UserError {
    data object AlreadyUsedEmailAddress : UserError()
    data object InsecurePassword : UserError()
}
sealed class TokenCreationError{
    data object UserOrPasswordAreInvalid : TokenCreationError()
}

data class TokenExternalInfo(val tokenValue: String, val tokenExpiration: Instant)

@Suppress("unused")
@Named
class UserService(
    private val trxManager: TransactionManager,
    val usersDomain: UsersDomain,
    private val clock: Clock
): UserService {
    override fun createUser(name: String, email: String, password: String): Either<UserError, User> {
        if (!usersDomain.isSafePassword(password)) {
            return failure(UserError.InsecurePassword)
        }

        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)

        return trxManager.run {
            if (repoUsers.findByEmail(email) != null) {
                return@run failure(UserError.AlreadyUsedEmailAddress)
            }
            val participant = repoUsers.createUser(name, email, passwordValidationInfo)
            success(participant)
        }
    }

    override fun createToken(email: String, password: String): Either<TokenCreationError, TokenExternalInfo> {
        if (email.isBlank() || password.isBlank()) {
            failure(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return trxManager.run {
            val user: User =
                repoUsers.findByEmail(email)
                    ?: return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            if (!usersDomain.validatePassword(password, user.passwordValidation)) {
                return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            }
            val tokenValue = usersDomain.generateTokenValue()
            val now = clock.now()
            val newToken =
                Token(
                    usersDomain.createTokenValidationInformation(tokenValue),
                    user.id,
                    createdAt = now,
                    lastUsedAt = now,
                )
            repoUsers.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
            success(
                TokenExternalInfo(
                    tokenValue,
                    usersDomain.getTokenExpiration(newToken),
                ),
            )
        }
    }

    override fun revokeToken(token: String): Boolean {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        return trxManager.run {
            repoUsers.removeTokenByValidationInfo(tokenValidationInfo)
            true
        }
    }

    override fun getUserByToken(token: String): User? {
        if (!usersDomain.canBeToken(token)) {
            return null
        }
        return trxManager.run {
            val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
            val userAndToken: Pair<User, Token>? = repoUsers.getTokenByTokenValidationInfo(tokenValidationInfo)
            if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
                repoUsers.updateTokenLastUsed(userAndToken.second, clock.now())
                userAndToken.first
            } else {
                null
            }
        }
    }
}