import org.junit.jupiter.api.Test
import pt.rs.Product
import pt.rs.Region
import kotlin.test.assertFailsWith

class ProductTests {
    @Test
    fun `create invalid product`(){
        assertFailsWith<IllegalArgumentException> {
            val sut = Product(
                1,
                1,
                "",
                " Test ",
                Region.CENTER,
                19.00,
                listOf("htt://dummyImage:8080/ "))
        }
        assertFailsWith<IllegalArgumentException> {
            val sut2 = Product(
                1,
                2,
                "Milk",
                " Test ",
                Region.AZORES,
                00.00,
                listOf("htt://dummyImage:8080/"))
        }
    }

    @Test
    fun `create valid product`(){
        val sut = Product(
            2,
            3,
            "Bread",
            "Home Made",
            Region.ALENTEJO,
            12.00,
            listOf("htt://dummyImage:8080/"))
        assert(sut.region == Region.ALENTEJO)
    }
}