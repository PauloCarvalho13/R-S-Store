package pt.rs.user

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}