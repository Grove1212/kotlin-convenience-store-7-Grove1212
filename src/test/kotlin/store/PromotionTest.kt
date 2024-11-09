package store

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PromotionTest {
    @Test
    fun `Promotion객체가 예외 없이 잘 만들어졌으면 true`() {
        assertDoesNotThrow {
            Promotion("탄산2+1", 2, 1, "2024-01-01", "2024-12-31")
        }
    }
}