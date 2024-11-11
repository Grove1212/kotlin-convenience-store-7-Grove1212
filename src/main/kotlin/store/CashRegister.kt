package store

import camp.nextstep.edu.missionutils.Console

class CashRegister(
    val products: List<Product>, //재고 관리 권한
    val promotions: List<Promotion>,
    val inputView: InputView,
    val outputView: OutputView
) {
    fun run() {
        outputView.displayProductInfo(products)
        outputView.displayProductNameAndQuantity()

    }

    fun checkOutOrder(input: String, inputView: InputView, outputView: OutputView) {
        val productNameAndQuantity = parseInput(input)
        val purchaseOrder = PurchaseOrder(changeInputToPurchaseStocks(productNameAndQuantity))

    }

    fun checkAddFreeProduct(inputView: InputView, outputView: OutputView) {
        purchasedStocks.forEachIndexed { index, purchasedStock ->
            if (purchasedStock.promotion == null) {
                return@forEachIndexed
            }
            if (purchasedStock.checkAddPromotionalProductForFree()) {
                outputView.addFreeProduct(purchasedStock.product.name)
                if (Console.readLine() == "Y") {
                    purchasedStocks[index] = purchasedStock.addPromotionalProduct()
                }
            }
        }
    }

    fun checkLackOfPromotionalStock(inputView: InputView, outputView: OutputView) {
        purchasedStocks.forEachIndexed { index, purchasedStock ->
            if (purchasedStock.promotion == null) {
                return@forEachIndexed
            }
            val lackOfStock = purchasedStock.checkCountLackOfStock()
            if (lackOfStock != 0) {
                outputView.purchaseWithoutDiscount(lackOfStock)
                val purchaseLackOfStock = Console.readLine()
                if (purchaseLackOfStock == "Y") {
                    purchasedStocks[index] = purchasedStock.setQuantityToProductQuantity()
                    val name = purchasedStock.product.name
                    val nonPromotionProduct = cashRegister.
                        ?: throw IllegalArgumentException("[ERROR] 찾는 상품이 없습니다.")

                    purchaseProducts.add(PurchasedStock(lackOfStock, nonPromotionProduct, null))
                }
                if (purchaseLackOfStock == "N") {
                    purchaseProducts[index] = it.setQuantityToProductQuantity()
                }
            }
        }
    }

    fun parseInput(input: String): List<Pair<String, Int>> {
        return input.split(",").map { it ->
            val cleaned = it.removeSurrounding("[", "]")
            val (name, quantity) = cleaned.split("-")
            name to quantity.toInt()
        }
    }

    fun changeInputToPurchaseStocks(input: List<Pair<String, Int>>): MutableList<PurchasedStock> {
        return input.map { (name, quantity) ->
            val product = findProduct(name)
            val promotion = findPromotion(product)

            PurchasedStock(quantity, product, promotion)
        }.toMutableList()
    }

    fun findProductForPriority(name: String): Product {
        return products.find { it.isPromotionalProduct(name) }
            ?: products.find { it.isNonPromotionalProduct(name) }
            ?: throw IllegalArgumentException("[ERROR] 그런 상품은 없습니다.")
    }

    fun findNonPromotionalProduct(name: String): Product {
        return products.find { it.isNonPromotionalProduct(name) } ?: throw IllegalStateException
    }

    fun findPromotion(product: Product) = promotions.find { it.isEligibleForPromotion(product) }
}
