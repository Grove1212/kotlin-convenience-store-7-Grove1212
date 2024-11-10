package store

import camp.nextstep.edu.missionutils.Console
import java.io.File

fun main() {
    // TODO: 프로그램 구현
    //1. 파일 가져오기
    val cashRegister = CashRegister()

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

    //7. 구매하기
    purchaseProducts.forEach { (product, quantity) ->
        product.purchase(quantity)
    }

    // 8. 총 구매액과 행사할인가 구하기
    val purchaseAmount = purchaseProducts.sumOf { (product, quantity) ->
        product.calculatePurchasedAmount(quantity)
    }

    val discountedAmount = purchaseProducts.sumOf { (product, quantity) ->
        val promotion = promotions.find { it.isEligibleForPromotion(product) }
        val promotionQuantity = promotion?.calculateNumberOfPromotionProduct(quantity) ?: 0
        product.calculatePromotionAmount(promotionQuantity)
    }


    //9. 멤버십 할인 여부 입력받기
    var membershipDiscount = 0
    println("멤버십 할인을 받으시겠습니까? (Y/N)")
    val membershipInput = Console.readLine()
    if (membershipInput == "Y") {
        membershipDiscount = ((purchaseAmount - discountedAmount) * 0.3).toInt()
        if (membershipDiscount > 8000) {
            membershipDiscount = 8000
        }
    }

    // 10. 최종 구매액 구하기
    val totalPurchaseAmount = purchaseAmount - discountedAmount - membershipDiscount

    //11. 영수증 만들어서 출력
    println("==============W 편의점================")
    println("상품명\t\t수량\t금액")
    println(purchaseProducts.toString()) // 하면 영수증용 상품명, 수량, 금액 리턴
    println("=============증\t정===============")
    purchaseProducts.forEach { (product, quantity) ->
        val promotion = promotions.find { it.isEligibleForPromotion(product) }
        val promotionQuantity = promotion?.calculateNumberOfPromotionProduct(quantity) ?: 0
        println("${product.name}\t${promotionQuantity}")
    }
    println("====================================")
    println("총구매액\t\t__\t${String.format("%,d",purchaseAmount)}\n" +
            "행사할인\t\t\t-${String.format("%,d",discountedAmount)}\n" +
            "멤버십할인\t\t\t-${String.format("%,d",membershipDiscount)}\n" +
            "내실돈\t\t\t ${String.format("%,d",totalPurchaseAmount)}")
}

fun parseInput(input: String, products: List<Product>): MutableList<Pair<Product, Int>> {
    return input.split(",").map { it ->
        val cleaned = it.removeSurrounding("[", "]")
        val (name, quantity) = cleaned.split("-")
        val product = products.find { it.name == name && it.promotion != null && it.quantity != 0 }
            ?: products.find { it.name == name }
            ?: throw IllegalArgumentException("[ERROR] 그런 상품은 없습니다.")
        product to quantity.toInt()
    }.toMutableList()
}
