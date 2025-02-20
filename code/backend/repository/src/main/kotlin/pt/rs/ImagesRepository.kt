package pt.rs

interface ImagesRepository: Repository<ImageDetails> {

    /**
     * Insert an image for a product
     * @param imageDetails the image to be inserted
     * @param productId the id of the product
     */
    fun insertImage(imageDetails: ImageDetails, productId: Int)

    /**
     * Find all images for a product
     * @param productId the id of the product
     * @return a list with all images for the product or an empty list if there are no images
     */
    fun findByProductId(productId: Int): List<ImageDetails>

    /**
     * Delete all images for a product
     * @param productId the id of the product
     */
    fun deleteByProductId(productId: Int)

}