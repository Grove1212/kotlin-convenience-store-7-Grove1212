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
        val discountAmount = ((purchasedStocks.sumOf { it.calculateNonPromotionalPaymentAmount() }) * 0.3)
        if (discountAmount > 8000){
            membershipDiscount = 8000
            return
        }
        membershipDiscount = discountAmount.toInt()
    }

    fun makeReceipt(): List<String> {
        val str = mutableListOf<String>()
        str.add(purchasedStocks.map { it.toString() }.joinToString("\n"))
        str.add(purchasedStocks.filter { it.promotion != null && it.countPromotionQuantity() != 0 }
            .map { "${it.product.name}\t${it.countPromotionQuantity()}" }
            .joinToString("\n"))
        str.add("${numberOfPurchasedStocks}")
        str.add("${String.format("%,d", paymentAmount)}")
        str.add("${String.format("%,d", promotionDiscount)}")
        str.add("${String.format("%,d", membershipDiscount ?: 0)}")
        str.add("${String.format("%,d", totalPaymentAmount)}")
        return str
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

        private fun calculateTotalPurchaseAmount(
            purchaseAmount: Int,
            promotionDiscount: Int,
            membershipDiscount: Int
        ): Int {
            return purchaseAmount - promotionDiscount - membershipDiscount
        }
    }
}