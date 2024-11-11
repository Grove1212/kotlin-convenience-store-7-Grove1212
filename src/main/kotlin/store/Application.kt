package store

import camp.nextstep.edu.missionutils.Console

fun main() {
    // TODO: 프로그램 구현
    //1. 파일 가져오기
    val inputView = InputView()
    val outputView = OutputView()
    val cashRegister = CashRegister(inputView.loadProducts(), inputView.loadPromotions(), inputView, outputView)

    //3. 프로그램 시작 문구 출력

    //4. 구매할 상품명과 수량 입력받기

    //5. 구매한 상품명과 수량 리스트로 만들기
    val purchaseProducts = cashRegister.makePurchaseOrder(input)

    //6. 상품들 중 프로모션 상품이 있는지 검사하고,
// 프로모션이라면 프로모션 상품 추가구매 할지 안할지 검사하고,
// 프로모션 재고가 없는데 일반 상품을 구매할지 안할지 검사한다.
    purchaseProducts.forEachIndexed { index, it ->
        if (it.promotion == null) {
            return@forEachIndexed
        }

        if (it.checkAddPromotionalProductForFree()) {
            outputView.addFreeProduct(it.product.name)
            if (inputView.getAnswerOfQuery() == "Y") {
                purchaseProducts[index] = it.addPromotionalProduct()
            }
        }

        val lackOfStock = it.checkCountLackOfStock()
        if (lackOfStock != 0) {
            outputView.purchaseWithoutDiscount(lackOfStock)
            val purchaseLackOfStock = inputView.getAnswerOfQuery()
            if (purchaseLackOfStock == "Y") {
                purchaseProducts[index] = it.setQuantityToProductQuantity()
                val name = it.product.name
                val nonPromotionProduct = cashRegister.products.find { it.isNonPromotionalProduct(name) }
                    ?: throw IllegalArgumentException("[ERROR] 찾는 상품이 없습니다.")

                purchaseProducts.add(PurchasedStock(lackOfStock, nonPromotionProduct, null))
            }
            if (purchaseLackOfStock == "N") {
                purchaseProducts[index] = it.setQuantityToProductQuantity()
            }
        }
    }

    //7. 구매하기
    purchaseProducts.forEach { it ->
        it.purchase()
    }

    // 8. 총 구매액과 행사할인가 구하기
    val purchaseAmount = purchaseProducts.sumOf { it ->
        it.buy
    }

    val discountedAmount = purchaseProducts.sumOf { it ->
        it.calculatePromotionAmount()
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
