package store

class PurchasedStock(
    var buy: Int,
    val product: Product,
    val promotion: Promotion? = null,
) {
    fun countAdditionalPromotionQuantity(): Int {
        return promotion?.calculateNumberOfPromotionProduct(buy) ?: 0
    }

    fun countPromotionQuantity(): Int {
        return promotion?.calculateNumberOfPromotionProduct(buy) ?: 0 * (promotion?.getBundle() ?: 0)
    }

    fun calculatePaymentAmount(): Int {
        return product.calculatePurchasedAmount(buy)
    }

    fun calculateNonPromotionalPaymentAmount(): Int =
        promotion?.let { 0 } ?: product.calculatePurchasedAmount(buy)

    fun calculatePromotionDiscountedByPayment(): Int {
        return product.calculatePurchasedAmount(countAdditionalPromotionQuantity())
    }

    fun purchase() {
        product.decreaseStock(buy)
    }

    fun setQuantityToProductQuantity(): PurchasedStock {
        buy = product.quantity
        return this
    }

    fun countAdditionalPromotionProductForFree(): Boolean {
        if (remainQuantity() < 1) {
            return false
        }
        return promotion?.canGetMoreProductsForFree(buy) ?: false
    }

    fun remainQuantity() = product.countLeftQuantity(buy)
    fun isStockLacking() = product.countLeftQuantity(buy) < 0
    fun addPromotionalProduct(): PurchasedStock {
        buy++
        return this
    }

    override fun toString(): String {
        return "${product.name}\t\t${buy}\t${String.format("%,d", calculatePaymentAmount())}"
    }
}