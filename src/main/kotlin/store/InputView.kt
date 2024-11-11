package store

import camp.nextstep.edu.missionutils.Console
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

    fun getProductAndQuantity(): String {
        while (true) {
            try {
                val input = Console.readLine() ?: throw IllegalArgumentException("[ERROR] 입력을 받을 수 없습니다.")
                require(input.matches(Regex("""\[\w+-\d+](, \[\w+-\d+])*"""))) {
                    "[ERROR] 예: [사이다-2],[감자칩-1]"
                }
                return input
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    fun getAnswerOfQuery(): String {
        while (true) {
            try {
                val input = Console.readLine()?.trim()?.uppercase()
                require(input != null) {throw IllegalArgumentException("[ERROR] Y 또는 N을 입력해주세요")}
                require (input == "Y" || input == "N") { throw IllegalArgumentException("[ERROR] Y 또는 N을 입력해주세요") }
                return input
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    companion object {
        private const val PRODUCTSPATH = "src/main/resources/products.md"
        private const val PROMOTIONSPATH = "src/main/resources/promotions.md"
    }
}