package pt.rs

import pt.rs.impl.Either
import pt.rs.impl.TokenCreationError
import pt.rs.impl.TokenExternalInfo
import pt.rs.impl.UserError
import pt.rs.user.User

interface UserService {

    /**
     * Create a new user
     * @param name User name
     * @param email User email
     * @param password User password
     * @return User or error
     */
    fun createUser(name:String, email:String, password:String): Either<UserError, User>

    /**
     * Create a new token
     * @param email User email
     * @param password User password
     * @return Token or error
     */
    fun createToken(email: String, password: String): Either<TokenCreationError, TokenExternalInfo>

    /**
     * Revoke the token
     * @param token Bearer token
     * @return true if success
     */
    fun revokeToken(token: String): Boolean

    /**
     * Get user by token
     * @param token Bearer token
     * @return User
     */
    fun getUserByToken(token: String): User?


}