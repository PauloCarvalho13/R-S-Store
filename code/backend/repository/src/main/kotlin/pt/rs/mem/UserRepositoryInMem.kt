package pt.rs.mem

import jakarta.inject.Named
import kotlinx.datetime.Instant
import pt.rs.UserRepository
import pt.rs.user.PasswordValidationInfo
import pt.rs.user.Token
import pt.rs.user.TokenValidationInfo
import pt.rs.user.User

@Suppress("unused")
@Named
class UserRepositoryInMem: UserRepository {

    private val users = mutableListOf<User>()
    private val tokens = mutableListOf<Token>()

    override fun createUser(name: String, email: String, password: PasswordValidationInfo): User {
        return User(users.size, name, email, password).also { users.add(it) }
    }

    override fun findByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? {
        return tokens.firstOrNull { it.tokenValidationInfo == tokenValidationInfo }?.let {
            val user = findById(it.userId)
            requireNotNull(user)
            user to it
        }
    }

    override fun createToken(token: Token, maxTokens: Int) {
        val nrOfTokens = tokens.count { it.userId == token.userId }

        // Remove the oldest token if we have achieved the maximum number of tokens
        if (nrOfTokens >= maxTokens) {
            tokens
                .filter { it.userId == token.userId }
                .minByOrNull { it.lastUsedAt }!!
                .also { tk -> tokens.removeIf { it.tokenValidationInfo == tk.tokenValidationInfo } }
        }
        tokens.add(token)
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        tokens.removeIf { it.tokenValidationInfo == token.tokenValidationInfo }
        tokens.add(token)
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        val count = tokens.count { it.tokenValidationInfo == tokenValidationInfo }
        tokens.removeAll { it.tokenValidationInfo == tokenValidationInfo }
        return count
    }

    override fun save(entity: User) {
        users.removeIf { it.id == entity.id }
        users.add(entity)
    }

    override fun findAll(): List<User> {
        return users.toList()
    }

    override fun findById(id: Int): User? {
        return users.find { it.id == id }
    }

    override fun delete(id: Int) {
        users.removeIf { it.id == id }
    }

    override fun clear() {
        users.clear()
    }
}