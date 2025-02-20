package pt.rs.jdbi

import org.jdbi.v3.core.Jdbi
import pt.rs.Transaction
import pt.rs.TransactionManager

class TransactionManagerJdbi(
    private val jdbi: Jdbi
): TransactionManager {
    override fun <R> run(block: Transaction.() -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = TransactionJdbi(handle)
            block(transaction)
        }
}