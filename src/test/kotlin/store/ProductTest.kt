package store

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.assertThrows

class ProductTest {
    private val product = Product("컵라면,1700,10,null");

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    fun `상품의 재고 수량이 사려는 수량보다 적으면 결제 불가능`(quantity: Int) {
        val canPayment = product.checkPayable(quantity)
        assertEquals(canPayment, false)
    }

    @ParameterizedTest
    @ValueSource(ints = [11, 12, 13, 14, 100])
    fun `상품의 재고 수량이 사려는 수량보다 적지 않으면 결제 가능`(quantity: Int) {
        val canPayment = product.checkPayable(quantity)
        assertEquals(canPayment, true)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    fun `고객이 상품을 구매하면 결제된 수량만큼 재고에서 차감한 재고를 받아온다`(quantity: Int) {
        val remainingQuantity = product.purchase(quantity)
        assertEquals(remainingQuantity, INITIALIZED_QUANTITY - quantity)
    }

    @Test
    fun `고객이 상품을 재고보다 많이 구매하면 에러 발생`() {
        val count = 12
        assertThrows<IllegalStateException> {
            product.purchase(count)
        }
    }

    companion object {
        const val INITIALIZED_NAME = "컵라면"
        const val INITIALIZED_PRICE = 1700
        const val INITIALIZED_QUANTITY = 10
        val INITIALIZED_PROMOTION = null
    }
}