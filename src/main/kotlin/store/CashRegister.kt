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
        val purchasedStocks = getPurchaseStocks(productNameAndQuantity)
        checkLackOfStock(purchasedStocks)
        checkPromotion(purchasedStocks)
        return PurchaseOrder(purchasedStocks)
    }

    private fun checkLackOfStock(stocks: MutableList<PurchasedStock>) {
        for (index in stocks.indices) {
            val stock = stocks[index]
            val quantity = stock.product.quantity + findNonPromotionalProduct(stock.product.name).quantity
            if (quantity < stock.buy) {
                handleLackOfStockPrompt(stock, index, stocks, quantity)
            }
        }
    }

    private fun checkPromotion(purchasedStocks: MutableList<PurchasedStock>) {
        checkAddFreeProduct(purchasedStocks)
        checkLackOfPromotionalStock(purchasedStocks)
    }

    private fun handleLackOfStockPrompt(stock: PurchasedStock, index: Int, stocks: MutableList<PurchasedStock>, quantity: Int) {
        outputView.purchaseOnlyProductQuantity(quantity)
        val answerPurchaseOrNot = inputView.getAnswerOfQuery()
        if (answerPurchaseOrNot == "Y") {
            purchaseLackOfStock(stock, stocks)
            return
        }
        stocks.removeAt(index)
    }

    private fun purchaseLackOfStock(stock: PurchasedStock, stocks: MutableList<PurchasedStock>) {
        if (stock.promotion != null) {
            addNonPromotionalStockForShortage(stocks, stock)
            return
        }
        stock.setQuantityToProductQuantity()
    }

    private fun checkAddFreeProduct(purchasedStocks: MutableList<PurchasedStock>) {
        for (index in purchasedStocks.indices) {
            val purchasedStock = purchasedStocks[index]
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
        for (index in purchasedStocks.indices) {
            val stock = purchasedStocks[index]
            if (stock.isStockLacking() && stock.promotion != null) {
                handleLackOfPromotionalStockPrompt(stock, index, purchasedStocks)
            }
        }
    }

    private fun handleLackOfPromotionalStockPrompt(
        stock: PurchasedStock,
        index: Int,
        stocks: MutableList<PurchasedStock>
    ) {
        outputView.purchaseWithoutDiscount(-stock.remainQuantity())
        val purchaseLackOfStock = inputView.getAnswerOfQuery()
        if (purchaseLackOfStock == "Y") {
            addNonPromotionalStockForShortage(stocks, stock)
        }
        stocks[index] = stock.setQuantityToProductQuantity()
    }

    private fun addNonPromotionalStockForShortage(stocks: MutableList<PurchasedStock>, shortageStock: PurchasedStock) {
        val nonPromotionProduct = findNonPromotionalProduct(shortageStock.product.name)
        val lackingAmount = -shortageStock.remainQuantity()
        shortageStock.setQuantityToProductQuantity()
        val nonPromotionStock = PurchasedStock(lackingAmount, nonPromotionProduct)
        if (nonPromotionStock.isStockLacking()) {
            nonPromotionStock.setQuantityToProductQuantity()
        }
        stocks.add(nonPromotionStock)
    }

    private fun parseInput(input: String): List<Pair<String, Int>> {
        return input.split(",").map {
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
        outputView.stockRunOut(name)
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
