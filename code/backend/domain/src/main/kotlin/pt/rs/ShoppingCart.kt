package pt.rs

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ShoppingCart(
    val cartId: Int,
    val userId: Int?, // Can be null in the case that the user don't have an account registered or its no logged in
    val creationTime: Instant = Clock.System.now(),
    val products: List<Product> = emptyList()
){
    fun addProduct(product: Product): ShoppingCart{
        check(product !in products) { "Product already exists in the cart" }
        return this.copy(products = products + product)
    }

    fun removeProduct(product: Product): ShoppingCart{
        check(product in products) { "Product does not exist in the cart" }
        return this.copy(products = products - product)
    }
}
