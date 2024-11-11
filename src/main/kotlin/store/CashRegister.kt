package store

class CashRegister(
    val products: List<Product>, //재고 관리 권한
    val promotions: List<Promotion>
) {
    fun makePurchaseOrder(input: String): MutableList<PurchasedStock> {
        val productNameAndQuantity = parseInput(input)
        return changeInputToPurchaseOrder(productNameAndQuantity).toMutableList()
    }

    fun parseInput(input: String): List<Pair<String, Int>> {
        return input.split(",").map { it ->
            val cleaned = it.removeSurrounding("[", "]")
            val (name, quantity) = cleaned.split("-")
            name to quantity.toInt()
        }
    }

    fun changeInputToPurchaseOrder(input: List<Pair<String, Int>>): List<PurchasedStock> {
        return input.map { (name, quantity) ->
            val product = products.find { it.isPromotionalProduct(name) }
                ?: products.find { it.isNonPromotionalProduct(name) }
                ?: throw IllegalArgumentException("[ERROR] 그런 상품은 없습니다.")

            val promotion = promotions.find { it.isEligibleForPromotion(product) }

            PurchasedStock(quantity, product, promotion)
        }
    }
}
