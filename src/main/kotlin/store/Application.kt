package store

import camp.nextstep.edu.missionutils.Console
import java.io.File

fun main() {
    // TODO: 프로그램 구현
    //1. 파일 가져오기
    val PRODUCTSPATH = "src/main/resources/products.md"
    val PROMOTIONSPATH = "src/main/resources/promotions.md"

    //2. product, promotion 객체 생성
    val products = File(PRODUCTSPATH).useLines { lines ->
        lines.drop(1).map { Product(it) }.toList()
    }
    val promotions = File(PROMOTIONSPATH).useLines { lines ->
        lines.drop(1).map { Promotion(it) }.toList()
    }

    //3. 프로그램 시작 문구 출력
    println(
        "안녕하세요. Grove1212편의점입니다.\n" +
                "현재 보유하고 있는 상품입니다.\n"
    )
    products.forEach { println(it.toString()) }

    //4. 구매할 상품명과 수량 입력받기
    println("\n구매할 상품명과 수량을 입력하세요 (예: [콜라-3],[에너지바-5]):")
    val input = Console.readLine() ?: throw IllegalArgumentException("[ERROR] null값 입력")

    //5. 구매한 상품명과 수량 리스트로 만들기
    val purchaseProducts = parseInput(input, products)

    //6. 상품들 중 프로모션 상품이 있는지 검사하고,
// 프로모션이라면 프로모션 상품 추가구매 할지 안할지 검사하고,
// 프로모션 재고가 없는데 일반 상품을 구매할지 안할지 검사한다.
    purchaseProducts.forEachIndexed { index, (product, quantity) ->
        val promotion = promotions.find { it.isEligibleForPromotion(product) }

        if (promotion != null) {
            if (promotion.canGetOneMoreProductsForFree(quantity)) {
                println("현재 ${product.name}은(는)이 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
                if (Console.readLine() == "Y") {
                    purchaseProducts[index] = product to (quantity + 1)
                }
            }

            val lackOfStock = product.countLackOfStock(quantity)
            if (lackOfStock != 0) {
                println("현재 ${lackOfStock}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
                val purchaseLackOfStock = Console.readLine()
                if (purchaseLackOfStock == "Y") {
                    purchaseProducts[index] = product to (product.quantity)
                    val nonPromotionProduct = products.find { product.name == it.name && it.promotion == null }
                        ?: throw IllegalArgumentException("[ERROR] 찾는 상품이 없습니다.")
                    purchaseProducts.add(Pair(nonPromotionProduct, lackOfStock))
                }
                if (purchaseLackOfStock == "N") {
                    purchaseProducts[index] = product to (quantity + lackOfStock)
                }

            }
        }
    }

    //7. 
}

fun parseInput(input: String, products: List<Product>): MutableList<Pair<Product, Int>> {
    return input.split(",").map { it ->
        val cleaned = it.removeSurrounding("[", "]")
        val (name, quantity) = cleaned.split("-")
        val product = products.find { it.name == name && it.promotion != null && it.quantity != 0}
            ?: products.find { it.name == name }
            ?: throw IllegalArgumentException("[ERROR] 그런 상품은 없습니다.")
        product to quantity.toInt()
    }.toMutableList()
}
