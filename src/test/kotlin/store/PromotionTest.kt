package store

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PromotionTest {
    val promotion = Promotion("탄산2+1,2,1,2024-01-01,2024-12-31")

    @Test
    fun `Promotion객체가 예외 없이 잘 만들어졌으면 true`() {
        assertDoesNotThrow {
            Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-12-31")
        }
    }

    @Test
    fun `Product가 해당 프로모션 할인 상품인 경우 true 반환`() {
        val product = Product("콜라,1000,10,탄산2+1")
        assertEquals(promotion.isEligibleForPromotion(product), true)
    }

    @Test
    fun `Product가 해당 프로모션 할인 상품이 아닌 경우 false 반환`() {
        val product = Product("콜라,1000,10,null")
        assertEquals(promotion.isEligibleForPromotion(product), false)
    }

    @Test
    fun `오늘 날짜가 프로모션 기간인 경우 true 반환`(){
        assertEquals(promotion.isPromotionOngoing(), true)
    }
}