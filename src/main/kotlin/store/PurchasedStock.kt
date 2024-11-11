package store

class PurchasedStock(
    var buy: Int,
    val product: Product,
    val promotion: Promotion? = null,
) {
    fun calculatePromotionAmount(): Int {
        return promotion?.calculateNumberOfPromotionProduct(buy) ?: 0
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

    fun getTotalNumberOfProduct(): Int {
        return 0
    }
}