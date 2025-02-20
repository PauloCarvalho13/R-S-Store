package pt.rs.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.slf4j.LoggerFactory
import pt.rs.UserRepository
import pt.rs.user.PasswordValidationInfo
import pt.rs.user.Token
import pt.rs.user.TokenValidationInfo
import pt.rs.user.User
import java.sql.ResultSet

class UserRepositoryJdbi(
    private val handle: Handle
): UserRepository {
    override fun createUser(name: String, email: String, password: PasswordValidationInfo): User {
        val id =
            handle
                .createUpdate(
                    """
            INSERT INTO dbo.users (name, email, password_validation) 
            VALUES (:name, :email, :password_validation)
            RETURNING id
            """,
                ).bind("name", name)
                .bind("email", email)
                .bind("password_validation", password.validationInfo)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return User(id, name, email, password)
    }

    override fun findByEmail(email: String): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE email = :email")
            .bind("email", email)
            .map(UserMapper())
            .findOne()
            .orElse(null)


    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle
            .createQuery(
                """
                SELECT id, name, email, password_validation, token_validation, created_at, last_used_at
                FROM dbo.Users as users 
                INNER JOIN dbo.Tokens as tokens 
                ON users.id = tokens.user_id
                WHERE token_validation = :validation_information
            """,
            ).bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    override fun createToken(token: Token, maxTokens: Int) {
        // Delete the oldest token when achieved the maximum number of tokens
        val deletions =
            handle
                .createUpdate(
                    """
                    delete from dbo.Tokens 
                    where user_id = :user_id 
                        and token_validation in (
                            select token_validation from dbo.Tokens where user_id = :user_id 
                                order by last_used_at desc offset :offset
                        )
                    """.trimIndent(),
                ).bind("user_id", token.userId)
                .bind("offset", maxTokens - 1)
                .execute()

        logger.info("{} tokens deleted when creating new token", deletions)

        handle
            .createUpdate(
                """
                insert into dbo.Tokens(user_id, token_validation, created_at, last_used_at) 
                values (:user_id, :token_validation, :created_at, :last_used_at)
                """.trimIndent(),
            ).bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("created_at", token.createdAt.epochSeconds)
            .bind("last_used_at", token.lastUsedAt.epochSeconds)
            .execute()
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle
            .createUpdate(
                """
                UPDATE dbo.Tokens
                SET last_used_at = :last_used_at
                WHERE token_validation = :validation_information
                """.trimIndent(),
            ).bind("last_used_at", now.epochSeconds)
            .bind("validation_information", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int =
        handle.createUpdate(
            """
                DELETE from dbo.Tokens
                WHERE token_validation = :validation_information
            """,
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .execute()


    override fun save(entity: User) {
        handle
            .createUpdate(
                """
            UPDATE dbo.users 
            SET name = :name, email = :email 
            WHERE id = :id
            """,
            ).bind("name", entity.name)
            .bind("email", entity.email)
            .bind("id", entity.id)
            .execute()
    }

    override fun findAll(): List<User> =
        handle
            .createQuery("SELECT * FROM dbo.users")
            .map(UserMapper())
            .list()

    override fun findById(id: Int): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .map(UserMapper())
            .findOne()
            .orElse(null)

    override fun delete(id: Int) {
        handle
            .createUpdate("DELETE FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.Tokens").execute()
        handle.createUpdate("DELETE FROM dbo.users").execute()
    }

    // Mapper for User
    private class UserMapper : RowMapper<User> {
        override fun map(
            rs: ResultSet,
            ctx: StatementContext,
        ): User =
            User(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                email = rs.getString("email"),
                passwordValidation = PasswordValidationInfo(rs.getString("password_validation")),
            )
    }

    private data class UserAndTokenModel(
        val id: Int,
        val name: String,
        val email: String,
        val passwordValidation: PasswordValidationInfo,
        val tokenValidation: TokenValidationInfo,
        val createdAt: Long,
        val lastUsedAt: Long,
    ) {
        val userAndToken: Pair<User, Token>
            get() =
                Pair(
                    User(id, name, email, passwordValidation),
                    Token(
                        tokenValidation,
                        id,
                        Instant.fromEpochSeconds(createdAt),
                        Instant.fromEpochSeconds(lastUsedAt),
                    ),
                )
    }
    companion object {
        private val logger = LoggerFactory.getLogger(UserRepositoryJdbi::class.java)
    }
}