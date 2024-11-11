package store

class PurchasedStock(
    var buy: Int,
    val product: Product,
    val promotion: Promotion? = null,
) {
    fun countPromotionQuantity(): Int {
        return promotion?.calculateNumberOfPromotionProduct(buy) ?: 0
    }

    fun calculatePaymentAmount(): Int {
        return product.calculatePurchasedAmount(buy)
    }

    fun calculateNonPromotionalPaymentAmount(): Int =
        promotion?.let { 0 } ?: product.calculatePurchasedAmount(buy)

    fun calculatePromotionDiscountedByPayment(): Int {
        return product.calculatePurchasedAmount(countPromotionQuantity())
    }

    fun purchase() {
        product.decreaseStock(buy)
    }

    fun setQuantityToProductQuantity(): PurchasedStock {
        buy = product.quantity
        return this
    }

    fun countAdditionalPromotionProductForFree(): Boolean {
        if (isStockLacking()) {
            return false
        }
        return promotion?.canGetMoreProductsForFree(buy) ?: false
    }

    fun lackingAmount() = product.countLackOfStock(buy)
    fun isStockLacking() = product.countLackOfStock(buy) > 0
    fun addPromotionalProduct(): PurchasedStock {
        buy++
        return this
    }

    override fun toString(): String {
        return "${product.name}\t\t${buy}\t${String.format("%,d", calculatePaymentAmount())}"
    }
}