package pt.rs.mem

import jakarta.inject.Named
import pt.rs.*
import pt.rs.user.User

@Named
class ProductsRepositoryInMem: ProductsRepository {
    private val products = mutableListOf<Product>()

    override fun createProduct(
        user: User,
        name: String,
        description: String,
        region: Region,
        price: Double,
        imagesDetails: List<ImageDetails>
    ): Product {
        val product = Product(
            id = products.size + 1,
            name = name,
            description = description,
            region = region,
            price = price,
            announcer = Announcer(
                userId = user.id,
                name = user.name,
                email = user.email
            ),
            imagesDetails = imagesDetails
        )
        products.add(product)
        return product
    }

    override fun filterProducts(region: Region?, minPrice: Double?, maxPrice: Double?): List<Product> {
        return products.filter { product ->
            (region == null || product.region == region) &&
            (minPrice == null || product.price >= minPrice) &&
            (maxPrice == null || product.price <= maxPrice)
        }
    }

    override fun findProductsByAnnouncer(announcer: Announcer): List<Product> {
        return products.filter { product -> product.announcer.userId == announcer.userId }
    }

    override fun save(entity: Product){
       products.removeIf { it.id == entity.id }
       products.add(entity)
    }

    override fun findAll(): List<Product> {
        return products.toList()
    }

    override fun findById(id: Int): Product? {
        return products.find { it.id == id }
    }

    override fun delete(id: Int) {
        products.removeIf { it.id == id }
    }

    override fun clear() {
        products.clear()
    }

}