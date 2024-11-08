package store

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ProductTest {
    private lateinit var product: Product

    @ParameterizedTest
    @ValueSource(ints = [1,2,3,4,5,6,7,8,9,10])
    fun `상품의 재고 수량이 사려는 수량보다 적으면 결제 불가능`(quantity: Int) {
        product = Product("컵라면,1700,10,null");
        val canPayment = product.checkPayable(quantity)
        assertEquals(canPayment, false)
    }

    @ParameterizedTest
    @ValueSource(ints = [11,12,13,14,100])
    fun `상품의 재고 수량이 사려는 수량보다 적지 않으면 결제 가능`(quantity: Int) {
        product = Product("컵라면,1700,10,null");
        val canPayment = product.checkPayable(quantity)
        assertEquals(canPayment, true)
    }
}