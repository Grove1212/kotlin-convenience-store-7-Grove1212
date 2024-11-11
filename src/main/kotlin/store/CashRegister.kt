package store

class CashRegister(
    val products: List<Product>, //재고 관리 권한
    val promotions: List<Promotion>,
    val inputView: InputView,
    val outputView: OutputView
) {
    fun run() {
        outputView.displayProductInfo(products)
        outputView.displayProductNameAndQuantity()
        val input = inputView.getProductAndQuantity()
        val purchaseOrder = checkOutOrder(input)
        checkMembershipDiscount(purchaseOrder)
        purchaseOrder.purchaseProducts()
        val str = purchaseOrder.makeReceipt()
        outputView.receipt(purchaseOrder.makeReceipt())
    }

    fun checkMembershipDiscount(purchaseOrder: PurchaseOrder) {
        outputView.membershipDiscount()
        if (inputView.getAnswerOfQuery() == "Y") {
            purchaseOrder.setMembershipDiscount()
        }
    }

    fun checkOutOrder(input: String): PurchaseOrder {
        val productNameAndQuantity = parseInput(input)
        var purchasedStocks = changeInputToPurchaseStocks(productNameAndQuantity)
        checkPromotion(purchasedStocks)
        return PurchaseOrder(purchasedStocks)
    }

    fun checkPromotion(purchasedStocks: MutableList<PurchasedStock>) {
        checkAddFreeProduct(purchasedStocks)
        checkLackOfPromotionalStock(purchasedStocks)
    }

    fun checkAddFreeProduct(purchasedStocks: MutableList<PurchasedStock>) {
        purchasedStocks.forEachIndexed { index, purchasedStock ->
            if (purchasedStock.countAdditionalPromotionProductForFree()) {
                handleFreeProductPrompt(purchasedStock, index, purchasedStocks)
            }
        }
    }

    fun handleFreeProductPrompt(stock: PurchasedStock, index: Int, stocks: MutableList<PurchasedStock>) {
        outputView.addFreeProduct(stock.product.name)
        if (inputView.getAnswerOfQuery() == "Y") {
            stocks[index] = stock.addPromotionalProduct()
        }
    }

    fun checkLackOfPromotionalStock(purchasedStocks: MutableList<PurchasedStock>) {
        purchasedStocks.forEachIndexed { index, stock ->
            if (stock.promotion == null) {
                return@forEachIndexed
            }
            if (stock.isStockLacking()) {
                handleLackOfStockPrompt(stock, index, purchasedStocks)
            }
        }
    }

    fun handleLackOfStockPrompt(stock: PurchasedStock, index: Int, stocks: MutableList<PurchasedStock>) {
        outputView.purchaseWithoutDiscount(stock.lackingAmount())
        val purchaseLackOfStock = inputView.getAnswerOfQuery()
        if (purchaseLackOfStock == "Y") {
            addNonPromotionalStockForShortage(stocks, stock)
        }
        stocks[index] = stock.setQuantityToProductQuantity()
    }

    fun addNonPromotionalStockForShortage(stocks: MutableList<PurchasedStock>, shortageStock: PurchasedStock) {
        val name = shortageStock.product.name
        val nonPromotionProduct = findNonPromotionalProduct(name)

        stocks.add(PurchasedStock(shortageStock.lackingAmount(), nonPromotionProduct))
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
            val product = findProductForPriority(name)
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
        return products.find { it.isNonPromotionalProduct(name) }
            ?: throw IllegalStateException("[ERROR] ${name}을 찾을 수 없습니다.")
    }

    fun findPromotion(product: Product) = promotions.find { it.isEligibleForPromotion(product) }
}
