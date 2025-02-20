package repoJdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import pt.rs.ImageDetails
import pt.rs.Region
import pt.rs.jdbi.ImagesRepositoryJdbi
import pt.rs.jdbi.ProductsRepositoryJdbi
import pt.rs.jdbi.UserRepositoryJdbi
import pt.rs.jdbi.configureWithAppRequirements
import pt.rs.toAnnouncer
import pt.rs.user.PasswordValidationInfo
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertFailsWith
import kotlin.collections.List

private fun newTokenValidationData() = "token-${abs(Random.nextLong())}"
private fun dummyImageDetails() = List(1) { ImageDetails(Random.nextLong().toString(), "htt://dummyImage:8080/$it") }

class RepositoryJdbiTests {

    companion object {
        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private val jdbi =
            Jdbi
                .create(
                    PGSimpleDataSource().apply {
                        setURL(Environment.getDbUrl())
                    },
                ).configureWithAppRequirements()
    }

    @BeforeEach
    fun clean() {
        runWithHandle { handle: Handle ->
            ImagesRepositoryJdbi(handle).clear()
            ProductsRepositoryJdbi(handle).clear()
            UserRepositoryJdbi(handle).clear()
        }
    }

    @Test
    fun `create a new product and verify update it's price`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            val product = repoProducts.createProduct(user, "Product 1", " ", Region.CENTER, 10.00, dummyImageDetails() )
            assert(repoProducts.findById(product.id)?.price == 10.00)
        }
    }

    @Test
    fun `create a new product and verify delete it`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            val product = repoProducts.createProduct(user, "Product 2", " ", Region.CENTER, 10.00, dummyImageDetails())
            assert(repoProducts.findById(product.id)?.price == 10.00)
            repoProducts.delete(product.id)
            assert(repoProducts.findById(product.id) == null)
        }
    }

    @Test
    fun `create products and verify find all`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            repoProducts.createProduct(user, "Product 3", " ", Region.LISBON_AND_TAGUS_VALLEY, 10.00, dummyImageDetails())
            repoProducts.createProduct(user, "Product 4", " ", Region.CENTER, 10.00, dummyImageDetails())
            assert(repoProducts.findAll().size == 2)
        }
    }

    @Test
    fun `create products and verify filter by region`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))


            repoProducts.createProduct(user, "Product 5", " ", Region.CENTER, 12.00, dummyImageDetails())
            repoProducts.createProduct(user, "Product 6", " ", Region.LISBON_AND_TAGUS_VALLEY, 10.00, dummyImageDetails())

            assert(repoProducts.filterProducts(Region.CENTER, null, null).size == 1)
            assert(repoProducts.filterProducts(Region.LISBON_AND_TAGUS_VALLEY, null, null).size == 1)
            assert(repoProducts.filterProducts(Region.PORTO_AND_NORTH, null, null).isEmpty())
        }
    }

    @Test
    fun `create products and verify filter by price`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            repoProducts.createProduct(user, "Product 7", " ", Region.CENTER, 10.00, dummyImageDetails())
            repoProducts.createProduct(user, "Product 8", " ", Region.LISBON_AND_TAGUS_VALLEY, 20.00, dummyImageDetails())

            assert(repoProducts.filterProducts(null, 15.00, 25.00).size == 1)
            assert(repoProducts.filterProducts(null, 5.00, 15.00).size == 1)
            assert(repoProducts.filterProducts(null, 25.00, 35.00).isEmpty())
        }
    }

    @Test
    fun `create products and verify filter by region and price`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            repoProducts.createProduct(user, "Product 9", " ", Region.CENTER, 10.00, dummyImageDetails())
            repoProducts.createProduct(user, "Product 10", " ", Region.LISBON_AND_TAGUS_VALLEY, 20.00, dummyImageDetails())

            assert(repoProducts.filterProducts(Region.LISBON_AND_TAGUS_VALLEY, 15.00, 25.00).size == 1)
            assert(repoProducts.filterProducts(Region.LISBON_AND_TAGUS_VALLEY, 5.00, 15.00).isEmpty())
            assert(repoProducts.filterProducts(Region.LISBON_AND_TAGUS_VALLEY, 25.00, 35.00).isEmpty())
        }
    }

    @Test
    fun `create products and verify find by announcer`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val greg = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            val alice = UserRepositoryJdbi(handle)
                .createUser("Alice", "alice@email.com", PasswordValidationInfo(newTokenValidationData()))

            repoProducts.createProduct(greg, "Product 11", " ", Region.CENTER, 10.00, dummyImageDetails())
            repoProducts.createProduct(alice, "Product 12", " ", Region.LISBON_AND_TAGUS_VALLEY, 20.00, dummyImageDetails())

            assert(repoProducts.findProductsByAnnouncer(greg.toAnnouncer()).size == 1)
            assert(repoProducts.findProductsByAnnouncer(alice.toAnnouncer()).size == 1)
            assert(repoProducts.findProductsByAnnouncer(greg.toAnnouncer())[0].name == "Product 11")
        }
    }

    @Test
    fun `create products and verify update product`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            val product = repoProducts.createProduct(user, "Product 13", " ", Region.CENTER, 10.00, dummyImageDetails())
            assert(repoProducts.findById(product.id)?.price == 10.00)
            repoProducts.save(product.copy(price = 20.00))
            assert(repoProducts.findById(product.id)?.price == 20.00)
        }
    }

    @Test
    fun `create products and verify update product with invalid price`() {
        runWithHandle { handle: Handle ->
            val repoProducts = ProductsRepositoryJdbi(handle)
            val user = UserRepositoryJdbi(handle)
                .createUser("Greg", "greg@email.com", PasswordValidationInfo(newTokenValidationData()))

            val product = repoProducts.createProduct(user, "Product 14", " ", Region.CENTER, 10.00, dummyImageDetails())
            assert(repoProducts.findById(product.id)?.price == 10.00)
            assertFailsWith<Exception> {
                repoProducts.save(product.copy(price = -20.00))
            }
        }
    }

}