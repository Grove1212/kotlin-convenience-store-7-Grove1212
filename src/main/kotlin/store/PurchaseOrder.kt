package store

import camp.nextstep.edu.missionutils.Console
import java.time.temporal.TemporalAmount

class PurchaseOrder(
    val purchasedStocks: MutableList<PurchasedStock>,
    val purchaseAmount: Int? = null,
    val promotionDiscount: Int? = null,
    val membershipDiscount: Int? = null,
    val totalPurchaseAmount: Int? = null
) {
    fun checkPromotion() {

    }
}