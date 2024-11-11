package store

class PurchaseOrder(
    var purchasedStocks: MutableList<PurchasedStock>,
) {
    val numberOfPurchasedStocks: Int
    val paymentAmount: Int
    val promotionDiscount: Int
    var membershipDiscount: Int = 0
    val totalPaymentAmount: Int

    init {
        numberOfPurchasedStocks = calculatenumberOfPurchasedStocks(purchasedStocks)
        paymentAmount = calculatepaymentAmount(purchasedStocks)
        promotionDiscount = calculatePromotionDiscount(purchasedStocks)
        totalPaymentAmount = calculateTotalPurchaseAmount(paymentAmount, promotionDiscount, membershipDiscount)
    }

    fun purchaseProducts() {
        purchasedStocks.forEach { it.purchase() }
    }

    fun setMembershipDiscount() {
        membershipDiscount = calculateMembershipDiscount(paymentAmount, promotionDiscount)
    }

    fun makeReceipt(): String {

    }

    companion object {
        private fun calculatenumberOfPurchasedStocks(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.buy }
        }

        private fun calculatepaymentAmount(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.calculatePaymentAmount() }
        }

        private fun calculatePromotionDiscount(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.calculatePromotionDiscountedByPayment() }
        }

        private fun calculateMembershipDiscount(purchaseAmount: Int, promotionDiscount: Int): Int {
            val discountAmount = ((purchaseAmount - promotionDiscount) * 0.3 / 100)
            if (discountAmount > 8000)
                return 8000
            return discountAmount.toInt()
        }

        private fun calculateTotalPurchaseAmount(
            purchaseAmount: Int,
            promotionDiscount: Int,
            membershipDiscount: Int
        ): Int {
            return purchaseAmount - promotionDiscount - membershipDiscount
        }
    }
}