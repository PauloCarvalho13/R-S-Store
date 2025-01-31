package repoMem

import org.junit.jupiter.api.Test
import pt.rs.Product
import pt.rs.Region
import pt.rs.ShoppingCart
import pt.rs.mem.ShoppingCartRepositoryInMem
import kotlin.test.assertFailsWith


class ShoppingCartRepoTests {
    private val repoShoppingCart = ShoppingCartRepositoryInMem()

    @Test
    fun`create shopping cart and add new product`(){
        val cart = repoShoppingCart.create(ShoppingCart(1, 1))
        val product = Product(1, 1,"product", "", Region.MADEIRA, 10.00)
        repoShoppingCart.addProductToCart(1, product)
        val cartWithProduct = repoShoppingCart.findById(1)
        assert(cartWithProduct?.products?.contains(product) == true)
    }

    @Test
    fun `create shopping cart and remove product`(){
        val cart = repoShoppingCart.create(ShoppingCart(1, 1))
        val product = Product(1, 1,"product", "", Region.MADEIRA, 10.00)
        repoShoppingCart.addProductToCart(1, product)
        repoShoppingCart.removeProductFromCart(1, product)
        val cartWithoutProduct = repoShoppingCart.findById(1)
        assert(cartWithoutProduct?.products?.contains(product) == false)
    }

    @Test
    fun `create shopping cart and remove product that does not exist`(){
        val cart = repoShoppingCart.create(ShoppingCart(1, 1))
        val product = Product(1, 1,"product", "", Region.MADEIRA, 10.00)
        repoShoppingCart.addProductToCart(1, product)
        val product2 = Product(2, 1,"product", "", Region.MADEIRA, 10.00)
        assertFailsWith<IllegalStateException> { repoShoppingCart.removeProductFromCart(1, product2) }
        val cartWithoutProduct = repoShoppingCart.findById(1)
        assert(cartWithoutProduct?.products?.contains(product) == true)
    }

    @Test
    fun `create shopping cart and add product that already exists`(){
        val cart = repoShoppingCart.create(ShoppingCart(3, 1))
        val product = Product(1, 5,"product", "", Region.MADEIRA, 10.00)
        repoShoppingCart.addProductToCart(3, product)
        assertFailsWith<IllegalStateException> {
            repoShoppingCart.addProductToCart(3, product)
        }
        val cartWithProduct = repoShoppingCart.findById(3)
        assert(cartWithProduct?.products?.size == 1)
    }
}