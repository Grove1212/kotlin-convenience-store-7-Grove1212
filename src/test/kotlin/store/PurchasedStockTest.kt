package store

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PurchasedStockTest{
    val purchasedStock = PurchasedStock(100, Product("콜라,10000,100,null"),null)

    @Test
    fun `0 세 개마다 콤마가 제대로 들어가고 있는지 테스트`(){
        assertEquals(purchasedStock.toString(), "콜라\t\t100\t1,000,000")
    }

}