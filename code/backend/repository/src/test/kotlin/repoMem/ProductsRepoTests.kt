package repoMem

import org.junit.jupiter.api.Test
import pt.rs.Product
import pt.rs.Region
import pt.rs.mem.ProductsRepositoryInMem

class ProductsRepoTests {
    private val repoProducts = ProductsRepositoryInMem()

    @Test
    fun `create a new product and verify update it's price`(){
        repoProducts.create(Product(1, 1,"Product 1"," " ,Region.CENTER, 10.00))
        assert(repoProducts.findById(1)?.price == 10.00)
        repoProducts.update(1, Product(1, 1,"Product 1"," " ,Region.CENTER, 20.00))
        assert(repoProducts.findById(1)?.price == 20.00)
    }

    @Test
    fun `create a new product and verify delete it`(){
        repoProducts.create(Product(1, 2,"Product 2"," " ,Region.CENTER, 10.00))
        assert(repoProducts.findById(2)?.price == 10.00)
        repoProducts.delete(2)
        assert(repoProducts.findById(2) == null)
    }

    @Test
    fun `create a new product and verify find it by region`(){
        repoProducts.create(Product(1, 3,"Product 3"," " ,Region.CENTER, 10.00))
        assert(repoProducts.findProductByRegion(Region.CENTER).size == 1)
    }


    @Test
    fun `create a new product and verify find all`(){
        repoProducts.create(Product(1, 5,"Product 5"," " ,Region.LISBON_AND_TAGUS_VALLEY, 10.00))
        assert(repoProducts.findAll().size == 1)
    }

}