import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.rs.Product
import pt.rs.Region
import pt.rs.TransactionManager
import pt.rs.impl.*
import pt.rs.jdbi.TransactionManagerJdbi
import pt.rs.jdbi.configureWithAppRequirements
import pt.rs.mem.TransactionManagerInMem
import pt.rs.user.Sha256TokenEncoder
import pt.rs.user.UsersDomain
import pt.rs.user.UsersDomainConfig
import java.util.stream.Stream
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes


class ProductServiceTests {
    companion object {
        private val jdbi =
            Jdbi
                .create(
                    PGSimpleDataSource().apply {
                        setURL(Environment.getDbUrl())
                    },
                ).configureWithAppRequirements()


        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
                TransactionManagerInMem().also { cleanup(it) },
            )

        private fun cleanup(trxManager: TransactionManager) {
            trxManager.run {
                repoProducts.clear()
                repoUsers.clear()
            }
        }
        private fun createUserService(
            trxManager: TransactionManager,
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3,
        ) = UserService(
            trxManager,
            UsersDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UsersDomainConfig(
                    tokenSizeInBytes = 256 / 8,
                    tokenTtl = tokenTtl,
                    tokenRollingTtl,
                    maxTokensPerUser = maxTokensPerUser,
                ),
            ),
            testClock,
        )
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `create product`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }

        val product = productService
            .createProduct(user,"Product 1", " ",Region.CENTER,10.00, emptyList())


        assertTrue(product is Success)
        assertIs<Product>(product.value)
        assertEquals("Product 1", product.value.name)
        assertEquals(10.0, product.value.price)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get product details`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val product = productService
            .createProduct(user, "Product 3", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val productDetails = productService
            .getProductDetails(product.id)

        assertTrue(productDetails is Success)
        assertIs<Product>(productDetails.value)
        assertEquals("Product 3", productDetails.value.name)

        val incorrectProductDetails = productService
            .getProductDetails(productDetails.value.id + 1)
        assertTrue(incorrectProductDetails is Failure)
        assertIs<ProductError.ProductNotFound>(incorrectProductDetails.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `update product`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val product = productService
            .createProduct(user, "Product 4", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val updatedProduct = productService
            .updateProduct(user, product.id, "Product 5", " ", 12.00, Region.CENTER, emptyList())

        assertTrue(updatedProduct is Success)
        assertIs<Product>(updatedProduct.value)
        assertEquals("Product 5", updatedProduct.value.name)
        assertEquals(12.0, updatedProduct.value.price)
        assertEquals(Region.CENTER, updatedProduct.value.region)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `update product with no permission and update a product that doesn't exist`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val product = productService
            .createProduct(user, "Product 6", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val user2 = userService
            .createUser("User 2", "user2@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val updatedProduct = productService
            .updateProduct(user2, product.id, "Product 7", " ", 12.00, Region.CENTER, emptyList())

        assertTrue(updatedProduct is Failure)
        assertIs<ProductError.NoPermissionToUpdateProduct>(updatedProduct.value)

        val incorrectProduct = productService
            .updateProduct(user, product.id + 1, "Product 8", " ", 13.00, Region.CENTER, emptyList())
        assertTrue(incorrectProduct is Failure)
        assertIs<ProductError.ProductNotFound>(incorrectProduct.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete product`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val product = productService
            .createProduct(user, "Product 9", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val deletedProduct = productService
            .deleteProduct(user, product.id)

        assertTrue(deletedProduct is Success)
        assertIs<Product>(deletedProduct.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `delete product with no permission and delete a product that doesn't exist`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val product = productService
            .createProduct(user, "Product 10", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val user2 = userService.createUser("User 2", "user2@gmai.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val deletedProduct = productService
            .deleteProduct(user2, product.id)
        assertTrue(deletedProduct is Failure)
        assertIs<ProductError.NoPermissionToDeleteProduct>(deletedProduct.value)

        val incorrectProduct = productService
            .deleteProduct(user, product.id + 1)
        assertTrue(incorrectProduct is Failure)
        assertIs<ProductError.ProductNotFound>(incorrectProduct.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `list products`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val product1 = productService
            .createProduct(user, "Product 11", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val product2 = productService
            .createProduct(user, "Product 12", " ", Region.ALENTEJO, 12.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val product3 = productService
            .createProduct(user, "Product 13", " ", Region.ALENTEJO, 13.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val products = productService
            .listProducts()

        assertTrue(products is Success)
        assertIs<List<Product>>(products.value)
        assertEquals(3, products.value.size)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `filter products`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val product1 = productService
            .createProduct(user, "Product 14", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val product2 = productService
            .createProduct(user, "Product 15", " ", Region.ALENTEJO, 12.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val product3 = productService
            .createProduct(user, "Product 16", " ", Region.ALENTEJO, 13.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val filteredProducts = productService
            .filterProducts(Region.ALENTEJO, 11.00, 12.00)

        assertTrue(filteredProducts is Success)
        assertIs<List<Product>>(filteredProducts.value)
        assertEquals(2, filteredProducts.value.size)

        val filteredProducts2 = productService
            .filterProducts(null, null, 11.00)

        assertTrue(filteredProducts2 is Success)
        assertIs<List<Product>>(filteredProducts2.value)
        assertEquals(1, filteredProducts2.value.size)

        val filteredProducts3 = productService
            .filterProducts(null, 12.00, null)

        assertTrue(filteredProducts3 is Success)
        assertIs<List<Product>>(filteredProducts3.value)
        assertEquals(2, filteredProducts3.value.size)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `get dashboard`(trxManager: TransactionManager) {
        val productService = ProductsService(trxManager)
        val userService = createUserService(trxManager, TestClock())

        val user = userService
            .createUser("User 1", "user1@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }
        val user2 = userService
            .createUser("User 2", "user2@gmail.com", "password")
            .let {
                check(it is Success)
                it.value
            }

        val product1 = productService
            .createProduct(user, "Product 17", " ", Region.ALENTEJO, 11.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val product2 = productService
            .createProduct(user, "Product 18", " ", Region.ALENTEJO, 12.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val product3 = productService
            .createProduct(user, "Product 19", " ", Region.ALENTEJO, 13.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val product4 = productService
            .createProduct(user2, "Product 20", " ", Region.ALENTEJO, 14.00, emptyList())
            .let {
                check(it is Success)
                it.value
            }
        val dashboard = productService
            .getDashboard(user)

        assertTrue(dashboard is Success)
        assertIs<List<Product>>(dashboard.value)
        assertEquals(3, dashboard.value.size)
    }
}