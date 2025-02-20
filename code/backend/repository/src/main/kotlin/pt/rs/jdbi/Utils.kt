package pt.rs.jdbi

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.rs.jdbi.mapper.InstantMapper
import pt.rs.jdbi.mapper.PasswordValidationInfoMapper
import pt.rs.jdbi.mapper.TokenValidationInfoMapper
import pt.rs.user.PasswordValidationInfo
import pt.rs.user.TokenValidationInfo


fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())
    registerColumnMapper(PasswordValidationInfo::class.java, PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfo::class.java, TokenValidationInfoMapper())
    registerColumnMapper(Instant::class.java, InstantMapper())
    return this
}