package pt.rs.jdbi

import org.jdbi.v3.core.Handle
import pt.rs.ProductsRepository
import pt.rs.ShoppingCartRepository
import pt.rs.Transaction
import pt.rs.UserRepository

class TransactionJdbi(
    private val handle: Handle
): Transaction {
    override val repoProducts: ProductsRepository = ProductsRepositoryJdbi(handle)

    override val repoShoppingCart: ShoppingCartRepository
        get() = throw NotImplementedError("Not implemented yet")

    override val repoUsers: UserRepository = UserRepositoryJdbi(handle)

    override fun rollback() {
        handle.rollback()
    }

}