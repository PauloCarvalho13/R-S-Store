package repoMem

import org.junit.jupiter.api.Test
import pt.rs.Announcer
import pt.rs.ImageDetails
import pt.rs.Product
import pt.rs.Region
import pt.rs.mem.ProductsRepositoryInMem
import pt.rs.user.PasswordValidationInfo
import pt.rs.user.User

private val DUMMY_IMAGE_DETAILS_LIST = listOf(ImageDetails("abc", "htt://dummyImage:8080/"))


class ProductsRepoTests {
    private val repoProducts = ProductsRepositoryInMem()
    private val user = User(1, "Greg", "greg@email.com", PasswordValidationInfo("1234") )
    private val announcer = Announcer(user.id, user.name, user.email)

    @Test
    fun `create a new product and verify update it's price`(){
        val product = repoProducts.createProduct(user,"Product 1", " ", Region.CENTER, 10.00, DUMMY_IMAGE_DETAILS_LIST )
        assert(repoProducts.findById(product.id)?.price == 10.00)
        repoProducts.save(Product(announcer, product.id,"Product 1"," ", Region.CENTER, 20.00))
        assert(repoProducts.findById(product.id)?.price == 20.00)
    }

    @Test
    fun `create a new product and verify delete it`(){
        val product = repoProducts.createProduct(user,"Product 2"," " , Region.CENTER, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        assert(repoProducts.findById(product.id)?.price == 10.00)
        repoProducts.delete(product.id)
        assert(repoProducts.findById(product.id) == null)
    }

    @Test
    fun `create a new product and verify find all`(){
        repoProducts.createProduct(user,"Product 5"," " , Region.LISBON_AND_TAGUS_VALLEY, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        assert(repoProducts.findAll().size == 1)
    }

    @Test
    fun `create a new product and verify filter by region`(){
        repoProducts.createProduct(user,"Product 3"," " , Region.CENTER, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        repoProducts.createProduct(user,"Product 4"," " , Region.LISBON_AND_TAGUS_VALLEY, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        assert(repoProducts.filterProducts(Region.CENTER, null, null).size == 1)
    }

    @Test
    fun `create a new product and verify filter by price`(){
        repoProducts.createProduct(user,"Product 6"," " , Region.CENTER, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        repoProducts.createProduct(user,"Product 7"," " , Region.LISBON_AND_TAGUS_VALLEY, 20.00, DUMMY_IMAGE_DETAILS_LIST)
        assert(repoProducts.filterProducts(null, 15.00, 25.00).size == 1)
    }

    @Test
    fun `create a new product and verify filter by region and price`(){
        repoProducts.createProduct(user,"Product 8"," " , Region.CENTER, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        repoProducts.createProduct(user,"Product 9"," " , Region.LISBON_AND_TAGUS_VALLEY, 20.00, DUMMY_IMAGE_DETAILS_LIST)
        assert(repoProducts.filterProducts(Region.LISBON_AND_TAGUS_VALLEY, 15.00, 25.00).size == 1)
    }

    @Test
    fun `create a new product and verify find by announcer`(){
        repoProducts.createProduct(user,"Product 10"," " , Region.CENTER, 10.00, DUMMY_IMAGE_DETAILS_LIST)
        repoProducts.createProduct(user,"Product 11"," " , Region.LISBON_AND_TAGUS_VALLEY, 20.00, DUMMY_IMAGE_DETAILS_LIST)
        assert(repoProducts.findProductsByAnnouncer(announcer).size == 2)
    }

}