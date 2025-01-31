package pt.rs

import pt.rs.user.User

interface ProductsRepository: Repository<Product>{
    /**
     * Create a new product
     * @param user the user that is creating the product
     * @param name the name of the product
     * @param description the description of the product
     * @param region the region of the product
     * @param price the price of the product
     * @param listOfImagesUrls the list of images urls of the product
     */
    fun createProduct(user: User, name: String, description: String, region: Region, price: Double, listOfImagesUrls:List<String> = emptyList()): Product

    /**
     * Find a product by the passed parameters
     * @param region the region of the product
     * @param minPrice the minimum price of the product
     * @param maxPrice the maximum price of the product
     * @return the list of products that match the parameters
     */
    fun filterProducts(region: Region?, minPrice: Double?, maxPrice: Double?): List<Product>

    /**
     * @param announcer that listed the products
     * @return the list of products that the announcer registered
     */
    fun findProductsByAnnouncer(announcer: Announcer) : List<Product>

}