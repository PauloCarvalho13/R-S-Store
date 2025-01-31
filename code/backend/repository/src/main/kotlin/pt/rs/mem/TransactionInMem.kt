package pt.rs.mem

import pt.rs.ProductsRepository
import pt.rs.ShoppingCartRepository
import pt.rs.Transaction
import pt.rs.UserRepository

class TransactionInMem(
    override val repoProducts: ProductsRepository,
    override val repoShoppingCart: ShoppingCartRepository,
    override val repoUsers: UserRepository
): Transaction {
    override fun rollback() = throw UnsupportedOperationException()
}