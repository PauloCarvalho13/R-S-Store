package pt.rs

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

@Suppress("unused")
@RestController
@RequestMapping("/rsStore")
class UsersController(
    private val userService: UserService,
    private val productService: ProductService
) {
    @PostMapping("/user")
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

    @PostMapping("/user/login")
    fun loginUser(@RequestBody uc: UserCredentials): ResponseEntity<Any> {
        return when (val tokenResult = userService.createToken(uc.email, uc.password)) {
            is Success -> ResponseEntity.status(HttpStatus.OK).body(tokenResult.value.tokenValue)
            is Failure -> Problem.UserOrPasswordAreInvalid.response(HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/user/logout")
    fun logoutUser(user: AuthenticatedUser) = userService.revokeToken(user.token)

    @GetMapping("/myDashboard")
    fun getMyDashboard(authUser: AuthenticatedUser): ResponseEntity<Any> {
        return when(val dashboard = productService.getDashboard(authUser.user)) {
            is Success -> ResponseEntity.ok(dashboard.value)
            is Failure -> throw IllegalStateException()
        }
    }
}