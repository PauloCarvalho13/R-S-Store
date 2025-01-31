package pt.rs

import kotlinx.datetime.Instant
import pt.rs.user.PasswordValidationInfo
import pt.rs.user.Token
import pt.rs.user.TokenValidationInfo
import pt.rs.user.User

interface UserRepository: Repository<User> {

    /**
     * Create a new user
     * @param name User name
     * @param email User email
     * @param password User password
     * @return User
     */
    fun createUser(name:String, email: String, password: PasswordValidationInfo): User

    /**
     * Find a user by email
     * @param email User email
     * @return User or null
     */
    fun findByEmail(email: String): User?

    /**
     * Find a token by validation info
     * @param tokenValidationInfo Token validation info
     * @return User and Token or null
     */
    fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo):Pair<User, Token>?

    /**
     * Create a new token
     * @param token Token
     * @param maxTokens Max tokens
     */
    fun createToken(token: Token, maxTokens: Int)

    /**
     * Update token last used
     * @param token Token
     * @param now Current time
     */
    fun updateTokenLastUsed(token: Token, now: Instant)

    /**
     * Remove token by validation info
     * @param tokenValidationInfo Token validation info
     * @return Number of tokens removed
     */
    fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int
}