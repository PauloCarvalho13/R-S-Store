package pt.rs.mem

import jakarta.inject.Named
import pt.rs.Product
import pt.rs.ShoppingCart
import pt.rs.ShoppingCartRepository
import pt.rs.user.User

@Named
class ShoppingCartRepositoryInMem: ShoppingCartRepository {
    private val shoppingCarts = mutableListOf<ShoppingCart>()

    override fun createShoppingCart(user: User): ShoppingCart {
        val shoppingCart = ShoppingCart(shoppingCarts.size + 1, user.id)
        shoppingCarts.add(shoppingCart)
        return shoppingCart
    }

    override fun addProductToCart(cartId: Int, product: Product) {
       val cart = shoppingCarts.find { it.cartId == cartId }
        check(cart != null) { "Cart does not exist" }
        shoppingCarts[shoppingCarts.indexOf(cart)] = cart.addProduct(product)
    }

    override fun removeProductFromCart(cartId: Int, product: Product) {
        val cart = shoppingCarts.find { it.cartId == cartId }
        check(cart != null) { "Cart does not exist" }
        shoppingCarts[shoppingCarts.indexOf(cart)] = cart.removeProduct(product)
    }

    override fun findCartByUser(user: User): ShoppingCart? {
        return shoppingCarts.find { it.userId == user.id }
    }

    override fun save(entity: ShoppingCart){
       shoppingCarts.removeIf { it.cartId == entity.cartId }
       shoppingCarts.add(entity)
    }


    override fun findAll(): List<ShoppingCart> {
        return shoppingCarts.toList()
    }

    override fun findById(id: Int): ShoppingCart? {
        return shoppingCarts.find { cart -> cart.cartId == id }
    }

    override fun delete(id: Int) {
       shoppingCarts.removeIf { cart -> cart.cartId == id }
    }

    override fun clear() {
        shoppingCarts.clear()
    }
}