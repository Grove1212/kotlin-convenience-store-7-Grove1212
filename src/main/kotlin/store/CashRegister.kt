package store

class CashRegister(
    private val products: List<Product>, //재고 관리 권한
    private val promotions: List<Promotion>,
    private val inputView: InputView,
    private val outputView: OutputView
) {
    fun run() {
        outputView.displayProductInfo(products)
        outputView.displayProductNameAndQuantity()
        val input = inputView.getProductAndQuantity()
        val purchaseOrder = checkOutOrder(input)
        checkMembershipDiscount(purchaseOrder)
        purchaseOrder.purchaseProducts()
        outputView.receipt(purchaseOrder.makeReceipt())
    }

    private fun checkOutOrder(input: String): PurchaseOrder {
        val productNameAndQuantity = parseInput(input) //getParser
        var purchasedStocks = getPurchaseStocks(productNameAndQuantity)
        checkPromotion(purchasedStocks)
        return PurchaseOrder(purchasedStocks)
    }

    private fun checkPromotion(purchasedStocks: MutableList<PurchasedStock>) {
        checkAddFreeProduct(purchasedStocks)
        checkLackOfPromotionalStock(purchasedStocks)
    }

    private fun checkAddFreeProduct(purchasedStocks: MutableList<PurchasedStock>) {
        purchasedStocks.forEachIndexed { index, purchasedStock ->
            if (purchasedStock.countAdditionalPromotionProductForFree()) {
                handleFreeProductPrompt(purchasedStock, index, purchasedStocks)
            }
        }
    }

    private fun handleFreeProductPrompt(stock: PurchasedStock, index: Int, stocks: MutableList<PurchasedStock>) {
        outputView.addFreeProduct(stock.product.name)
        if (inputView.getAnswerOfQuery() == "Y") {
            stocks[index] = stock.addPromotionalProduct()
        }
    }

    private fun checkLackOfPromotionalStock(purchasedStocks: MutableList<PurchasedStock>) {
        purchasedStocks.forEachIndexed { index, stock ->
            if (stock.promotion == null) {
                return@forEachIndexed
            }
            if (stock.isStockLacking()) {
                handleLackOfStockPrompt(stock, index, purchasedStocks)
            }
        }
    }

    private fun handleLackOfStockPrompt(stock: PurchasedStock, index: Int, stocks: MutableList<PurchasedStock>) {
        if (stock.product.quantity == 0) {
            outputView.stockRunOut(stock.product.name)
            stocks.removeAt(index)
        }
        outputView.purchaseWithoutDiscount(stock.lackingAmount())
        val purchaseLackOfStock = inputView.getAnswerOfQuery()
        if (purchaseLackOfStock == "Y") {
            addNonPromotionalStockForShortage(stocks, stock)
        }
        stocks[index] = stock.setQuantityToProductQuantity()
    }

    private fun addNonPromotionalStockForShortage(stocks: MutableList<PurchasedStock>, shortageStock: PurchasedStock) {
        val name = shortageStock.product.name
        val nonPromotionProduct = findNonPromotionalProduct(name)

        stocks.add(PurchasedStock(shortageStock.lackingAmount(), nonPromotionProduct))
    }

    private fun parseInput(input: String): List<Pair<String, Int>> {
        return input.split(",").map { it ->
            val cleaned = it.removeSurrounding("[", "]")
            val (name, quantity) = cleaned.split("-")
            name to quantity.toInt()
        }
    }

    private fun getPurchaseStocks(input: List<Pair<String, Int>>): MutableList<PurchasedStock> {
        return input.mapNotNull { (name, quantity) ->
            getPurchaseStock(name, quantity)
        }.toMutableList()
    }

    private fun getPurchaseStock(name: String, quantity: Int): PurchasedStock? {
        val product = findProductForPriority(name)
        val promotion = findPromotion(product)
        if (product.quantity != 0) {
            return PurchasedStock(quantity, product, promotion)
        }
        return null
    }

    private fun findProductForPriority(name: String): Product {
        return products.find { it.isPromotionalProduct(name) }
            ?: products.find { it.isNonPromotionalProduct(name) }
            ?: throw IllegalArgumentException("[ERROR] 그런 상품은 없습니다.")
    }

    private fun findNonPromotionalProduct(name: String): Product {
        return products.find { it.isNonPromotionalProduct(name) }
            ?: throw IllegalStateException("[ERROR] ${name}을 찾을 수 없습니다.")
    }

    private fun findPromotion(product: Product) = promotions.find { it.isEligibleForPromotion(product) }

    private fun checkMembershipDiscount(purchaseOrder: PurchaseOrder) {
        outputView.membershipDiscount()
        if (inputView.getAnswerOfQuery() == "Y") {
            purchaseOrder.setMembershipDiscount()
        }
    }
}
