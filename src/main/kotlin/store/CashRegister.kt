package store

import java.io.File

class CashRegister(
    private val products: List<Product> = loadProducts(),
    private val promotions: List<Promotion> = loadPromotions()
) {
    companion object {
        private const val PRODUCTSPATH = "src/main/resources/products.md"
        private const val PROMOTIONSPATH = "src/main/resources/promotions.md"

        private fun loadProducts(): List<Product> {
            return File(PRODUCTSPATH).useLines { lines ->
                lines.drop(1).map { Product(it) }.toList()
            }
        }

        private fun loadPromotions(): List<Promotion> {
            return File(PROMOTIONSPATH).useLines { lines ->
                lines.drop(1).map { Promotion(it) }.toList()
            }
        }
    }
}