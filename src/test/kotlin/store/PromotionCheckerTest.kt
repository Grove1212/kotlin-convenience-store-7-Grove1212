package store

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PromotionCheckerTest {
    @Test
    fun `해당 상품이 프로모션 대상인 경우 true 반환`() {
        val promotions = mutableListOf<Promotion>()
        promotions.add(Promotion("탄산2+1,2,1,2024-01-01,2024-12-31"))
        promotions.add(Promotion("MD추천상품,1,1,2024-01-01,2024-12-31"))
        promotions.add(Promotion("반짝할인,1,1,2024-11-01,2024-11-30"))
        val promotionchecker = PromotionChecker(promotions)
    }
}