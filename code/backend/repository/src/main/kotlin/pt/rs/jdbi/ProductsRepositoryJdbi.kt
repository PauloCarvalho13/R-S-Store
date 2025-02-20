package pt.rs.jdbi

import org.jdbi.v3.core.Handle
import pt.rs.*
import pt.rs.user.User
import java.sql.ResultSet

class ProductsRepositoryJdbi(
    private val handle: Handle
): ProductsRepository {
    private val repoUsers = UserRepositoryJdbi(handle)
    private val repoImages = ImagesRepositoryJdbi(handle)

    override fun createProduct(
        user: User,
        name: String,
        description: String,
        region: Region,
        price: Double,
        imagesDetails: List<ImageDetails>
    ): Product {
        val productId = handle.createUpdate(
            """
            INSERT INTO dbo.products (user_id, name, description, region, price)
            VALUES (:user_id, :name, :description, :region, :price)
            """
        ).bind("user_id", user.id)
            .bind("name", name)
            .bind("description", description)
            .bind("region", region)
            .bind("price", price)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

        imagesDetails.forEach { image -> repoImages.insertImage(image, productId) }

        return Product(
            id = productId,
            announcer = Announcer(
                userId = user.id,
                name = user.name,
                email = user.email
            ),
            name = name,
            description = description,
            region = region,
            price = price,
            imagesDetails = imagesDetails
        )
    }



    override fun filterProducts(region: Region?, minPrice: Double?, maxPrice: Double?): List<Product> =
        handle.createQuery(
            """
            SELECT * FROM dbo.products
            WHERE (:region IS NULL OR region = :region)
            AND (:minPrice IS NULL OR price >= :minPrice)
            AND (:maxPrice IS NULL OR price <= :maxPrice)
            """
        )
            .bind("region", region?.name)
            .bind("minPrice", minPrice)
            .bind("maxPrice", maxPrice)
            .map { rs, _ -> mapRowToProduct(rs) }
            .list()

    override fun findProductsByAnnouncer(announcer: Announcer): List<Product> =
        handle.createQuery(
            """
            SELECT * FROM dbo.products
            WHERE user_id = :user_id
            """
        )
            .bind("user_id", announcer.userId)
            .map { rs, _ -> mapRowToProduct(rs) }
            .list()

    override fun save(entity: Product) {
        handle.createUpdate(
            """
            UPDATE dbo.products
            SET name = :name, description = :description, region = :region, price = :price
            WHERE id = :id
            """
        )
            .bind("id", entity.id)
            .bind("name", entity.name)
            .bind("description", entity.description)
            .bind("region", entity.region)
            .bind("price", entity.price)
            .execute()

        repoImages.deleteByProductId(entity.id)

        entity.imagesDetails.forEach { image -> repoImages.insertImage(image, entity.id) }
    }

    override fun findAll(): List<Product> =
        handle.createQuery("SELECT * FROM dbo.products")
            .map { rs, _ -> mapRowToProduct(rs) }
            .list()

    override fun findById(id: Int): Product? =
        handle.createQuery("SELECT * FROM dbo.products WHERE id = :id")
            .bind("id", id)
            .map { rs, _ -> mapRowToProduct(rs) }
            .findFirst()
            .orElse(null)


    override fun delete(id: Int) {
        repoImages.deleteByProductId(id)

        handle.createUpdate("DELETE FROM dbo.products WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        repoImages.clear()
        handle.createUpdate("DELETE FROM dbo.products").execute()
    }

    private fun mapRowToProduct(rs: ResultSet): Product{
        val announcer = repoUsers.findById(rs.getInt("user_id"))
        requireNotNull(announcer) { "Announcer not found" }
        val images = repoImages.findByProductId(rs.getInt("id"))
        return Product(
            id = rs.getInt("id"),
            announcer = Announcer(
                userId = announcer.id,
                name = announcer.name,
                email = announcer.email
            ),
            name = rs.getString("name"),
            description = rs.getString("description"),
            region = Region.valueOf(rs.getString("region")),
            price = rs.getDouble("price"),
            imagesDetails = images
        )
    }

}