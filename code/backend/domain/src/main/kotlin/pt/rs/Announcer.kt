package pt.rs

data class Announcer(val userId: Int, val name: String, val email: String){
    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(email.isNotBlank()) { "email must not be blank" }
    }
}
