import kotlinx.datetime.Clock
import org.junit.jupiter.api.Test
import pt.rs.Product
import pt.rs.Region
import pt.rs.ShoppingCart

class ShoppingCartTests {
    @Test
    fun `adding new item to shopping cart`(){
        val sut = ShoppingCart(1, null, Clock.System.now())
        val prod = Product(1, 1, "Biscuits", " ", Region.AZORES, 5.00)
        val shWithProd = sut.addProduct(prod)
        assert(shWithProd.products.contains(prod))
    }

    @Test
    fun `removing item from shopping cart`(){
        val sut = ShoppingCart(1, null, Clock.System.now())
        val prod = Product(1, 1, "Biscuits", " ", Region.AZORES, 5.00)
        val shWithProd = sut.addProduct(prod)
        assert(shWithProd.products.contains(prod))
        val emptySh = shWithProd.removeProduct(prod)
        assert(emptySh.products.isEmpty())
    }
}