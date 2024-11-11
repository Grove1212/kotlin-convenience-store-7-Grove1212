package store

class PurchaseOrder(
    var purchasedStocks: MutableList<PurchasedStock>,
) {
    val numberOfPurchasedStocks: Int
    val paymentAmount: Int
    val promotionDiscount: Int
    var membershipDiscount: Int = 0
    var totalPaymentAmount: Int

    init {
        numberOfPurchasedStocks = calculateNumberOfPurchasedStocks(purchasedStocks)
        paymentAmount = calculatePaymentAmount(purchasedStocks)
        promotionDiscount = calculatePromotionDiscount(purchasedStocks)
        totalPaymentAmount = calculateTotalPurchaseAmount(paymentAmount, promotionDiscount)
    }

    fun purchaseProducts() {
        purchasedStocks.forEach { it.purchase() }
    }

    fun setMembershipDiscount() {
        val discountAmount = ((purchasedStocks.sumOf { it.calculateNonPromotionalPaymentAmount() }) * 0.3)
        if (discountAmount > 8000) {
            membershipDiscount = 8000
            totalPaymentAmount -= membershipDiscount
            return
        }
        membershipDiscount = discountAmount.toInt()
        totalPaymentAmount -= membershipDiscount
    }

    fun makePurchasedStocksToString(): String {
        val aggregatedSet = purchasedStocks.aggregateByProductNameToSet()
        return aggregatedSet.joinToString("\n") { it.toString() }
    }

    fun makeReceipt(): List<String> = listOf(
        makePurchasedStocksToString(),
        purchasedStocks
            .filter { it.promotion != null && it.countAdditionalPromotionQuantity() != 0 }
            .joinToString("\n") { "${it.product.name}\t\t${it.countAdditionalPromotionQuantity()}" },
        "$numberOfPurchasedStocks",
        String.format("%,d", paymentAmount),
        String.format("%,d", promotionDiscount),
        String.format("%,d", membershipDiscount),
        String.format("%,d", totalPaymentAmount)
    )

    fun List<PurchasedStock>.aggregateByProductNameToSet(): List<PurchasedStock> {
        val aggregatedMap = mutableMapOf<String, PurchasedStock>()
        this.forEach { item ->
            aggregatedMap.getOrPut(item.product.name) { PurchasedStock(0, item.product, null) }
                .apply { buy += item.buy }
        }
        return aggregatedMap.values.toList()
    }

    companion object {
        private fun calculateNumberOfPurchasedStocks(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.buy }
        }

        private fun calculatePaymentAmount(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.calculatePaymentAmount() }
        }

        private fun calculatePromotionDiscount(stocks: MutableList<PurchasedStock>): Int {
            return stocks.sumOf { it.calculatePromotionDiscountedByPayment() }
        }

        private fun calculateTotalPurchaseAmount(
            purchaseAmount: Int,
            promotionDiscount: Int
        ): Int {
            return purchaseAmount - promotionDiscount
        }
    }
}