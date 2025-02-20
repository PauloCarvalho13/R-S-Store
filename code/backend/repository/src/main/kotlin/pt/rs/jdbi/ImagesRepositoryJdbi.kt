package pt.rs.jdbi

import org.jdbi.v3.core.Handle
import pt.rs.ImageDetails
import pt.rs.ImagesRepository

class ImagesRepositoryJdbi(
    private val handle: Handle
) : ImagesRepository {

    override fun insertImage(imageDetails: ImageDetails, productId: Int) {
        handle.createUpdate(
            "INSERT INTO dbo.images (id, product_id, url) VALUES (:id ,:product_id, :url)"
        )
            .bind("id", imageDetails.id)
            .bind("product_id", productId)
            .bind("url", imageDetails.uri)
            .execute()
    }

    override fun findByProductId(productId: Int): List<ImageDetails> =
        handle.createQuery(
            "SELECT id, url FROM dbo.images WHERE product_id = :product_id"
        )
            .bind("product_id", productId)
            .map { rs, _ -> ImageDetails(rs.getString("id"), rs.getString("url")) }
            .list()

    override fun deleteByProductId(productId: Int) {
        handle.createUpdate("DELETE FROM dbo.images WHERE product_id = :product_id")
            .bind("product_id", productId)
            .execute()
    }

    override fun save(entity: ImageDetails) {
        handle.createUpdate(
            """
            UPDATE dbo.images
            SET url = :url
            WHERE id = :id
            """
        )
            .bind("id", entity.id)
            .bind("url", entity.uri)
            .execute()
    }

    override fun findAll(): List<ImageDetails> =
        handle.createQuery("SELECT id, url FROM dbo.images")
            .map { rs, _ -> ImageDetails(rs.getString("id"), rs.getString("url")) }
            .list()

    override fun findById(id: Int): ImageDetails? =
        handle.createQuery("SELECT id, url FROM dbo.images WHERE id = :id")
            .bind("id", id)
            .map { rs, _ -> ImageDetails(rs.getString("id"), rs.getString("url")) }
            .findFirst()
            .orElse(null)

    override fun delete(id: Int) {
        handle.createUpdate("DELETE FROM dbo.images WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.images").execute()
    }
}
