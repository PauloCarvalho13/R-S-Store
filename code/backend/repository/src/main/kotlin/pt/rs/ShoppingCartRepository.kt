package pt.rs

import pt.rs.user.User

interface ShoppingCartRepository: Repository<ShoppingCart> {

    /**
     * @param user that is going to be used to create the shopping cart
     * @return the shopping cart that was created
     */
    fun createShoppingCart(user: User): ShoppingCart

    /**
     * @param cartId cart id that we are adding the new product
     * @param product new products that is going to be added to the cart
     */
    fun addProductToCart(cartId: Int, product: Product)

    /**
     * @param cartId indicates the cart from where the product is going to be removed
     * @param product the products that it's going to be removed
     */
    fun removeProductFromCart(cartId: Int, product: Product)

    /**
     * @param user that is going to be searched in the repository
     * @return the shopping cart that belongs to the user
     */
    fun findCartByUser(user: User): ShoppingCart?
}