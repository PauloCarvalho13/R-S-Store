package pt.rs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.rs.impl.Failure
import pt.rs.impl.ProductError
import pt.rs.impl.Success
import pt.rs.model.*
import pt.rs.user.AuthenticatedUser

@Suppress("unused")
@RestController
@RequestMapping("/api")
class ProductsController(
    private val productService: ProductService
) {

    @Operation(summary = "Get all products")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of products or empty list"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @GetMapping("/products", produces = ["application/json"])
    fun getProducts(): ResponseEntity<Any> {
        return when(val products = productService.listProducts()) {
            is Success -> ResponseEntity.ok(products.value.map { product ->
                ProductOverview(
                    product.id,
                    product.name,
                    product.price,
                    product.region,
                    product.imagesDetails.first()
                )
            })
            is Failure -> Problem.InternalServerError.response(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Get products by query")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of products or empty list"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @PostMapping("/products/search", consumes = ["application/json"],produces = ["application/json"])
    fun getProductsByQuery(
        @RequestBody query: ProductInputQuery
    ): ResponseEntity<Any> {
        return when(val products = productService.filterProducts(query.region, query.minPrice, query.maxPrice)) {
            is Success -> ResponseEntity.ok(products.value.map { product ->
                ProductOverview(
                    product.id,
                    product.name,
                    product.price,
                    product.region,
                    product.imagesDetails.first()
                )
            })
            is Failure -> Problem.InternalServerError.response(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Get product by id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Product details"),
        ApiResponse(responseCode = "404", description = "Product not found")
    ])
    @GetMapping("/products/{id}", produces = ["application/json"])
    fun getProductById(@PathVariable id: Int): ResponseEntity<Any> {
        return when(val product = productService.getProductDetails(id)) {
            is Success -> ResponseEntity.ok(product.value.let {
                ProductDetails(
                    it.announcer,
                    it.id,
                    it.name,
                    it.description,
                    it.price,
                    it.region,
                    it.imagesDetails
                )
            })
            is Failure -> Problem.ProductNotFound.response(HttpStatus.NOT_FOUND)
        }
    }

    @Operation(summary = "Create product")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Product created"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @PostMapping("/products", consumes = ["application/json"], produces = ["application/json"])
    fun createProduct(@RequestBody prod: ProductInput, announcer: AuthenticatedUser): ResponseEntity<Any> {
        return when(val product = productService.createProduct(announcer.user, prod.name, prod.description, prod.region,  prod.price, prod.listOfImagesUrls)) {
            is Success -> ResponseEntity.ok(product.value.let {
                ProductDetails(
                    it.announcer,
                    it.id,
                    it.name,
                    it.description,
                    it.price,
                    it.region,
                    it.imagesDetails
                )
            })
            is Failure -> Problem.InternalServerError.response(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Edit product")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Product updated"),
        ApiResponse(responseCode = "404", description = "Product not found"),
        ApiResponse(responseCode = "403", description = "No permission to update product")
    ])
    @PutMapping("/products/{id}", consumes = ["application/json"], produces = ["application/json"])
    fun editProduct(@PathVariable id: Int, @RequestBody prod: ProductInput, authUser: AuthenticatedUser): ResponseEntity<Any> {
       return when(val updated = productService.updateProduct(authUser.user, id, prod.name, prod.description, prod.price, prod.region, prod.listOfImagesUrls)) {
            is Success -> ResponseEntity.ok(updated.value.let {
                ProductDetails(
                    it.announcer,
                    it.id,
                    it.name,
                    it.description,
                    it.price,
                    it.region,
                    it.imagesDetails
                )
            })
            is Failure -> when(updated.value) {
                is ProductError.NoPermissionToUpdateProduct -> Problem.NoPermissionToUpdateProduct.response(HttpStatus.FORBIDDEN)
                else -> Problem.ProductNotFound.response(HttpStatus.NOT_FOUND)
            }
       }
    }

    @Operation(summary = "Delete product")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Product deleted"),
        ApiResponse(responseCode = "404", description = "Product not found"),
        ApiResponse(responseCode = "403", description = "No permission to delete product")
    ])
    @DeleteMapping("/products/{id}", produces = ["application/json"])
    fun deleteProduct(@PathVariable id: Int, authUser: AuthenticatedUser): ResponseEntity<Any> {
        return when(val deleted = productService.deleteProduct(authUser.user, id)) {
            is Success -> ResponseEntity.ok(deleted.value.let {
                ProductDetails(
                    it.announcer,
                    it.id,
                    it.name,
                    it.description,
                    it.price,
                    it.region,
                    it.imagesDetails
                )
            })
            is Failure -> when(deleted.value) {
                is ProductError.NoPermissionToDeleteProduct -> Problem.NoPermissionToDeleteProduct.response(HttpStatus.FORBIDDEN)
                else -> Problem.ProductNotFound.response(HttpStatus.NOT_FOUND)
            }
        }
    }

    @Operation(summary = "Get my dashboard")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of products or empty list"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    @GetMapping("/myDashboard", produces = ["application/json"])
    fun getMyDashboard(authUser: AuthenticatedUser): ResponseEntity<Any> {
        return when(val dashboard = productService.getDashboard(authUser.user)) {
            is Success -> ResponseEntity.ok(dashboard.value.map {
                SellerProductInfo(
                    it.name,
                    it.description,
                    it.price,
                    it.region,
                    it.imagesDetails
                )
            })
            is Failure -> Problem.InternalServerError.response(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}