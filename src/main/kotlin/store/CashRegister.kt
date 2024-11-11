package store

import camp.nextstep.edu.missionutils.Console

class CashRegister(
    private val products: List<Product>, //재고 관리 권한
    private val promotions: List<Promotion>,
    private val inputView: InputView,
    private val outputView: OutputView
) {
    fun run() {
        var purchaseDecision = true
        while (purchaseDecision) {
            val purchaseOrder = validStockInput()
            checkMembershipDiscount(purchaseOrder)
            purchaseOrder.purchaseProducts()
            outputView.receipt(purchaseOrder.makeReceipt())
            purchaseDecision = makeMorePurchaseDecision()
        }
    }

    fun validStockInput():PurchaseOrder {
        try {
            val input = set()
            return checkOutOrder(input)
        } catch (e: IllegalStateException){
            println(e.message)
            return validStockInput()
        }
    }

    fun set(): String {
        outputView.displayProductInfo(products)
        outputView.displayProductNameAndQuantity()
        return inputView.getProductAndQuantity()
    }

    fun makeMorePurchaseDecision(): Boolean {
        outputView.displayPurchaseDecision()
        if (Console.readLine() == "Y") {
            return true
        }
        return false
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
            val nonPromotionalProductQuantity = findNonPromotionalProduct(stock.product.name)?.quantity ?: 0
            val quantity = stock.product.quantity + nonPromotionalProductQuantity
            if (quantity < stock.buy) {
                throw IllegalStateException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.")
            }
        }
    }

    private fun checkPromotion(purchasedStocks: MutableList<PurchasedStock>) {
        checkAddFreeProduct(purchasedStocks)
        checkLackOfPromotionalStock(purchasedStocks)
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
        outputView.purchaseWithoutDiscount(stock.product.name, stock.countPromotionQuantity())
        val purchaseLackOfStock = inputView.getAnswerOfQuery()
        if (purchaseLackOfStock == "Y") {
            addNonPromotionalStockForShortage(stocks, stock)
        }
        stocks[index] = stock.setQuantityToProductQuantity()
    }

    private fun addNonPromotionalStockForShortage(stocks: MutableList<PurchasedStock>, shortageStock: PurchasedStock) {
        val nonPromotionProduct = findNonPromotionalProduct(shortageStock.product.name) ?: return
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

    private fun findNonPromotionalProduct(name: String): Product? {
        return products.find { it.isNonPromotionalProduct(name) }
    }

    private fun findPromotion(product: Product) = promotions.find { it.isEligibleForPromotion(product) }

    private fun checkMembershipDiscount(purchaseOrder: PurchaseOrder) {
        outputView.membershipDiscount()
        if (inputView.getAnswerOfQuery() == "Y") {
            purchaseOrder.setMembershipDiscount()
        }
    }
}
