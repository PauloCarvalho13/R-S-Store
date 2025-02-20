package pt.rs

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

    @GetMapping("/products")
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

    @PostMapping("/products/search")
    fun getProductsByQuery(@RequestBody query: ProductInputQuery): ResponseEntity<Any> {
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

    @GetMapping("/products/{id}")
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

    @PostMapping("/products")
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

    @PutMapping("/products/{id}")
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

    @DeleteMapping("/products/{id}")
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

    @GetMapping("/myDashboard")
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