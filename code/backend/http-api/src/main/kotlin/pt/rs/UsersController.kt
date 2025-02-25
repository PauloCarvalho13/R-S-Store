package pt.rs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import pt.rs.impl.Failure
import pt.rs.impl.Success
import pt.rs.impl.UserError
import pt.rs.user.AuthenticatedUser
import pt.rs.model.Problem
import pt.rs.model.UserCreationInput
import pt.rs.model.UserCredentials
import pt.rs.model.UserInfo

@Suppress("unused")
@RestController
@RequestMapping("/api")
class UsersController(
    private val userService: UserService
) {

    @Operation(summary = "Create a new user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "User created"),
        ApiResponse(responseCode = "422", description = "User already exists"),
        ApiResponse(responseCode = "406", description = "Insecure password")
    ])
    @PostMapping("/user", consumes = ["application/json"])
    fun createUser(@RequestBody ui: UserCreationInput): ResponseEntity<*> {
        return when (val user = userService.createUser(ui.name,ui.email, ui.password)) {
            is Success -> {
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User created")
            }
            is Failure -> {
                when(user.value) {
                    is UserError.InsecurePassword -> Problem.InsecurePassword.response(HttpStatus.NOT_ACCEPTABLE)
                    else -> Problem.UserAlreadyExists.response(HttpStatus.UNPROCESSABLE_ENTITY)
                    //request is well-formed but the server is unable to process it
                }
            }
        }
    }

    @Operation(summary = "Login user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Login successful"),
        ApiResponse(responseCode = "400", description = "User or password are invalid")
    ])
    @PostMapping("/login", consumes = ["application/json"])
    fun loginUser(@RequestBody uc: UserCredentials): ResponseEntity<Any> {
        return when (val tokenResult = userService.createToken(uc.email, uc.password)) {
            is Success -> {
                val token = tokenResult.value.tokenValue
                ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, createAuthCookie(token))
                    .body("Login successful")
            }
            is Failure -> Problem.UserOrPasswordAreInvalid.response(HttpStatus.BAD_REQUEST)
        }
    }

    @Operation(summary = "Logout user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Logout successful")
    ])
    @PostMapping("/logout", consumes = ["application/json"])
    fun logoutUser(user: AuthenticatedUser): ResponseEntity<Any> {
        userService.revokeToken(user.token)
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, clearAuthCookie())
            .body("Logout successful")
    }

    @Operation(summary = "Get user info")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User info")
    ])
    @GetMapping("/user", produces = ["application/json"])
    fun userInfo(authUser: AuthenticatedUser): ResponseEntity<Any> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(UserInfo(
                authUser.user.id,
                authUser.user.name,
                authUser.user.email
            ))
    }

    private fun createAuthCookie(token: String): String {
        return "auth_token=$token; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=3600"
    }

    private fun clearAuthCookie(): String {
        return "auth_token=; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=0"
    }
}