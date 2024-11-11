package store

class PurchasedStock(
    var buy: Int,
    val product: Product,
    val promotion: Promotion? = null,
) {
    fun calculatePromotionAmount(): Int {
        return promotion?.calculateNumberOfPromotionProduct(buy) ?: 0
    }

    fun calculatePaymentAmount(): Int {
        return product.calculatePurchasedAmount(buy)
    }

    fun calculatePromotionDiscountedByPayment(): Int {
        return product.calculatePromotionAmount(calculatePromotionAmount())
    }

    fun purchase() {
        product.decreaseStock(buy)
    }

    fun setQuantityToProductQuantity(): PurchasedStock {
        buy = product.quantity
        return this
    }

    fun checkAddPromotionalProductForFree(): Boolean {
        return promotion?.canGetMoreProductsForFree(buy) ?: false
    }

    fun lackingAmount() = product.countLackOfStock(buy)
    fun isStockLacking() = product.countLackOfStock(buy) > 0
    fun addPromotionalProduct(): PurchasedStock {
        buy++
        return this
    }

    override fun toString(): String {
        return "${product.name}\t\t${buy}\t${String.format("%,d",calculatePaymentAmount())}"
    }
}