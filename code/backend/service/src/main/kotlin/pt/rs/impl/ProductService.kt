package pt.rs.impl

import jakarta.inject.Named
import pt.rs.*
import pt.rs.user.User

sealed class ProductError{
    data object ProductNotFound: ProductError()
    data object NoPermissionToUpdateProduct: ProductError()
    data object NoPermissionToDeleteProduct: ProductError()
}

@Suppress("unused")
@Named
class ProductsService(
    private val trxManager: TransactionManager
): ProductService {

    override fun createProduct(
        user: User,
        name: String,
        description: String,
        region: Region,
        price: Double,
        listOfImagesUrls: List<String>
    ): Either<ProductError, Product> = trxManager.run {
        val product = repoProducts.createProduct(user, name, description, region, price, listOfImagesUrls)
        success(product)
    }

    override fun getProductDetails(productId: Int): Either<ProductError, Product> = trxManager.run {
        val product = repoProducts.findById(productId) ?: return@run failure(ProductError.ProductNotFound)
        success(product)
    }

    override fun updateProduct(
        user: User,
        announcer: Announcer,
        id: Int,
        name: String,
        description: String,
        price: Double,
        region: Region,
        listOfImagesUrls: List<String>
    ): Either<ProductError, Product> = trxManager.run {
        val product = repoProducts.findById(id) ?: return@run failure(ProductError.ProductNotFound)
        if (announcer.userId != user.id) return@run failure(ProductError.NoPermissionToUpdateProduct)
        val updatedProduct = product.copy(
            name = name,
            description = description,
            price = price,
            region = region,
            listOfImagesUrls = listOfImagesUrls
        )
        repoProducts.save(updatedProduct)
        success(updatedProduct)
    }

    override fun deleteProduct(user: User, productId: Int): Either<ProductError, Product> = trxManager.run {
        val product = repoProducts.findById(productId) ?: return@run failure(ProductError.ProductNotFound)
        if (product.announcer.userId != user.id) return@run failure(ProductError.NoPermissionToDeleteProduct)
        repoProducts.delete(product.id)
        success(product)
    }

    override fun listProducts(): Either<ProductError, List<Product>> = trxManager.run {
        val products = repoProducts.findAll()
        success(products)
    }

    override fun filterProducts(
        region: Region?,
        minPrice: Double?,
        maxPrice: Double?
    ): Either<ProductError, List<Product>> = trxManager.run {
        val products = repoProducts.filterProducts(region, minPrice, maxPrice)
        success(products)
    }


    override fun getDashboard(user: User): Either<ProductError, List<Product>> = trxManager.run {
        val products = repoProducts.findProductsByAnnouncer(Announcer(userId = user.id, name = user.name, email = user.email))
        success(products)
    }
}