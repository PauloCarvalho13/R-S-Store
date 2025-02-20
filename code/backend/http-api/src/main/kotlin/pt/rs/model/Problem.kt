package pt.rs.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

private const val MEDIA_TYPE = "application/problem+json"
private const val PROBLEM_URI_PATH =
    "https://github.com/"

sealed class Problem(typedUri:URI){

    @Suppress("unused")
    val type = typedUri.toString()
    val title = typedUri.toString().split("/").last()

    fun response(status: HttpStatus): ResponseEntity<Any> =
        ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body(this)

    // Product problems
    data object ProductNotFound : Problem(URI("${PROBLEM_URI_PATH}/product-not-found"))
    data object NoPermissionToUpdateProduct : Problem(URI("${PROBLEM_URI_PATH}/no-permission-to-update-product"))
    data object NoPermissionToDeleteProduct : Problem(URI("${PROBLEM_URI_PATH}/no-permission-to-delete-product"))
    data object NoPermissionToCreateProduct : Problem(URI("${PROBLEM_URI_PATH}/no-permission-to-create-product"))

    // User problems
    data object UserOrPasswordAreInvalid : Problem(URI("${PROBLEM_URI_PATH}/user-or-password-are-invalid"))
    data object InsecurePassword : Problem(URI("${PROBLEM_URI_PATH}/insecure-password"))
    data object UserAlreadyExists : Problem(URI("${PROBLEM_URI_PATH}/user-already-exists"))

    // Request problems
    data object InvalidRequestContent : Problem(URI("${PROBLEM_URI_PATH}/invalid-request-content"))

    // Internal problems
    data object InternalServerError : Problem(URI("${PROBLEM_URI_PATH}/internal-server-error"))

}

