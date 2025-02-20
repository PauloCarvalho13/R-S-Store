package pt.rs.jdbi.mapper

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.rs.user.PasswordValidationInfo
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.jvm.Throws

class PasswordValidationInfoMapper: ColumnMapper<PasswordValidationInfo> {
    @Throws(SQLException::class)
    override fun map(
        r: ResultSet,
        columnNumber: Int,
        ctx: StatementContext?,
    ): PasswordValidationInfo = PasswordValidationInfo(r.getString(columnNumber))
}