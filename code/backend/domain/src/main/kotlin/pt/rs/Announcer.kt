package pt.rs

import pt.rs.user.User

data class Announcer(val userId: Int, val name: String, val email: String){
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(email.isNotBlank()) { "Email must not be blank" }
    }
}
fun User.toAnnouncer() = Announcer(id, name, email)