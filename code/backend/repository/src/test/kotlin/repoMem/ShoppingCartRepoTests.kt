package repoMem

import org.junit.jupiter.api.Test
import pt.rs.ImageDetails
import pt.rs.Region
import pt.rs.mem.ProductsRepositoryInMem
import pt.rs.mem.ShoppingCartRepositoryInMem
import pt.rs.user.PasswordValidationInfo
import pt.rs.user.User
import kotlin.test.assertFailsWith

private val DUMMY_IMAGE_DETAILS_LIST = listOf(ImageDetails("abc", "htt://dummyImage:8080/"))


class ShoppingCartRepoTests {
    private val repoShoppingCart = ShoppingCartRepositoryInMem()
    private val repoProduct = ProductsRepositoryInMem()

    private val user = User(1, "John", "john@gmail.com", PasswordValidationInfo("12345"))

    @Test
    fun`create shopping cart and add new product`(){
        val cart = repoShoppingCart.createShoppingCart(user)
        val product = repoProduct.createProduct(user,"product", "", Region.MADEIRA, 10.00, DUMMY_IMAGE_DETAILS_LIST )
        repoShoppingCart.addProductToCart(cart.cartId, product)
        val cartWithProduct = repoShoppingCart.findById(cart.cartId)
        assert(cartWithProduct?.products?.contains(product) == true)
    }

    @Test
    fun `create shopping cart and remove product`(){
        val cart = repoShoppingCart.createShoppingCart(user)
        val product = repoProduct.createProduct(user, "product", "", Region.MADEIRA, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        repoShoppingCart.addProductToCart(cart.cartId, product)
        repoShoppingCart.removeProductFromCart(cart.cartId, product)
        val cartWithoutProduct = repoShoppingCart.findById(1)
        assert(cartWithoutProduct?.products?.contains(product) == false)
    }

    @Test
    fun `create shopping cart and remove product that does not exist`(){
        val cart = repoShoppingCart.createShoppingCart(user)
        val product = repoProduct.createProduct(user,"product", "", Region.MADEIRA, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        repoShoppingCart.addProductToCart(cart.cartId, product)
        val product2 = repoProduct.createProduct(user,"product", "", Region.MADEIRA, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        assertFailsWith<IllegalStateException> { repoShoppingCart.removeProductFromCart(1, product2) }
        val cartWithoutProduct = repoShoppingCart.findById(cart.cartId)
        assert(cartWithoutProduct?.products?.contains(product) == true)
    }

    @Test
    fun `create shopping cart and add product that already exists`(){
        val cart = repoShoppingCart.createShoppingCart(user)
        val product = repoProduct.createProduct(user,"product", "", Region.MADEIRA, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        repoShoppingCart.addProductToCart(cart.cartId, product)
        assertFailsWith<IllegalStateException> {
            repoShoppingCart.addProductToCart(cart.cartId, product)
        }
        val cartWithProduct = repoShoppingCart.findById(cart.cartId)
        assert(cartWithProduct?.products?.size == 1)
    }
}