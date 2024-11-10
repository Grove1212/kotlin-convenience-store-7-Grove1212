package store

import java.io.File

class InputView {
    fun loadProducts(): List<Product> {
        return File(PRODUCTSPATH).useLines { lines ->
            lines.drop(1).map { Product(it) }.toList()
        }
    }

    fun loadPromotions(): List<Promotion> {
        return File(PROMOTIONSPATH).useLines { lines ->
            lines.drop(1).map { Promotion(it) }.toList()
        }
    }

    companion object {
        private const val PRODUCTSPATH = "src/main/resources/products.md"
        private const val PROMOTIONSPATH = "src/main/resources/promotions.md"
    }
}