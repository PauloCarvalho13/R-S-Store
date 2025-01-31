package pt.rs.mem

import pt.rs.Transaction
import pt.rs.TransactionManager

@Suppress("unused")
class TransactionManagerInMem: TransactionManager {
    private val repoProducts = ProductsRepositoryInMem()
    private val repoShoppingCart = ShoppingCartRepositoryInMem()
    private val repoUsers = UserRepositoryInMem()

    override fun <R> run(block: Transaction.() -> R): R
        = block(TransactionInMem(repoProducts, repoShoppingCart, repoUsers))
}