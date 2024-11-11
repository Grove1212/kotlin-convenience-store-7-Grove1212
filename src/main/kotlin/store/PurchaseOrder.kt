package store

class PurchaseOrder(
    var purchasedStocks: MutableList<PurchasedStock>,
) {
    val purchaseAmount: Int
    val promotionDiscount: Int
    var membershipDiscount: Int
    val totalPurchaseAmount: Int

    init {
        purchaseAmount = calculatePurchaseAmount(purchasedStocks)
        promotionDiscount = calculatePromotionDiscount(purchasedStocks)
        membershipDiscount = calculateMembershipDiscount(purchaseAmount, promotionDiscount)
        totalPurchaseAmount = calculateTotalPurchaseAmount(purchaseAmount, promotionDiscount, membershipDiscount)
    }

    fun setMembershipDiscountTOZero() {
        membershipDiscount = 0
    }

    companion object {
        private fun calculatePurchaseAmount(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.product.calculatePurchasedAmount(it.buy) }
        }

        private fun calculatePromotionDiscount(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.calculatePromotionAmount() }
        }

        private fun calculateMembershipDiscount(purchaseAmount: Int, promotionDiscount: Int): Int {
            val discountAmount = ((purchaseAmount - promotionDiscount) * 0.3 / 100)
            if(discountAmount > 8000)
                return 8000
            return discountAmount.toInt()
        }

        private fun calculateTotalPurchaseAmount(purchaseAmount: Int, promotionDiscount: Int, membershipDiscount: Int): Int {
            return purchaseAmount - promotionDiscount - membershipDiscount
        }
    }
}