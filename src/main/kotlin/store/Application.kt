package store

import camp.nextstep.edu.missionutils.Console
import java.io.File

fun main() {
    // TODO: 프로그램 구현
    //1. 파일 가져오기
    val inputView = InputView()
    val outputView = OutputView()
    val cashRegister = CashRegister(inputView.loadProducts(), inputView.loadPromotions())

    //3. 프로그램 시작 문구 출력
    outputView.displayProductInfo(cashRegister.products)


    //4. 구매할 상품명과 수량 입력받기
    outputView.displayProductNameAndQuantity()
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
                outputView.addFreeProduct(product.name)
                if (Console.readLine() == "Y") {
                    purchaseProducts[index] = product to (quantity + 1)
                }
            }

            val lackOfStock = product.countLackOfStock(quantity)
            if (lackOfStock != 0) {
                outputView.purchaseWithoutDiscount(lackOfStock)
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
    outputView.membershipDiscount()
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
    val promotionItems = purchaseProducts.sumOf { (product, quantity) ->
        val promotion = promotions.find { it.isEligibleForPromotion(product) }
        val promotionQuantity = promotion?.calculateNumberOfPromotionProduct(quantity) ?: 0
        "${product.name}\t${promotionQuantity}"
    }

    outputView.receipt(
        purchaseProducts.toString(),
        promotionItems,
        __,
        purchaseAmount,
        discountedAmount,
        membershipDiscount,
        totalPurchaseAmount
    )
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
