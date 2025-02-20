package pt.rs

import pt.rs.impl.Either
import pt.rs.impl.ProductError
import pt.rs.user.User

interface ProductService {

    /**
     * Create a new product
     * @param user the user creating the product
     * @param name the name of the product
     * @param description the description of the product
     * @param region the region of the product
     * @param price the price of the product
     * @param imagesDetails the list of images urls of the product
     * @return either the created product or an error
     */
    fun createProduct(
        user: User,
        name: String,
        description: String,
        region: Region,
        price:Double,
        imagesDetails: List<ImageDetails>
    ): Either<ProductError, Product>

    /**
     * Get the details of a product
     * @param productId the id of the product
     * @return either the product details or an error
     */
    fun getProductDetails(productId: Int): Either<ProductError, Product>

    /**
     * Update the details of a product
     * @param user the user updating the product
     * @param id the id of the product
     * @param name the name of the product
     * @param description the description of the product
     * @param price the price of the product
     * @param region the region of the product
     * @param imagesDetails the list of images urls of the product
     * @return either the updated product or an error
     */
    fun updateProduct(
        user: User,
        id:Int,
        name: String,
        description: String,
        price: Double,
        region: Region,
        imagesDetails: List<ImageDetails>
    ): Either<ProductError, Product>

    /**
     * Delete a product
     * @param user the user deleting the product
     * @param productId the id of the product to delete
     * @return either the deleted product or an error
     */
    fun deleteProduct(user: User, productId: Int): Either<ProductError, Product>

    /**
     * List all products
     * @return either a list of products or an error
     */
    fun listProducts(): Either<ProductError, List<Product>>

    /**
     * List all products by region and price range
     * @param region the region of the products
     * @param minPrice the minimum price of the products
     * @param maxPrice the maximum price of the products
     * @return either a list of products or an error
     */
    fun filterProducts(region: Region?, minPrice: Double?, maxPrice: Double?): Either<ProductError, List<Product>>

    /**
     * List all announced products
     * @param user the announcer of the products
     * @return either a list of products or an error
     */
    fun getDashboard(user: User): Either<ProductError, List<Product>>
}