package store

class OutputView {
    fun displayProductInfo(products: List<Product>) {
        println(
            "안녕하세요. Grove1212편의점입니다.\n" +
                    "현재 보유하고 있는 상품입니다.\n"
        )
        products.forEach { println(it.toString()) }
    }

    fun displayProductNameAndQuantity() {
        println("\n구매할 상품명과 수량을 입력하세요 (예: [콜라-3],[에너지바-5]):")
    }

    fun addFreeProduct(productName: String) {
        println("현재 ${productName}은(는)이 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
    }

    fun purchaseWithoutDiscount(lackOfStock: Int) {
        println("현재 ${lackOfStock}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
    }

    fun membershipDiscount() {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
    }

    fun receipt(
        purchaseProducts: String,
        promotionItems: String,
        totalNumberOfPurchasedItem: Int,
        purchaseAmount: Int,
        discountedAmount: Int,
        membershipDiscount: Int,
        totalPurchaseAmount: Int
    ) {
        println("==============W 편의점================")
        println("상품명\t\t수량\t금액")
        println(purchaseProducts) // 영수증용 상품명, 수량, 금액 출력
        println("=============증\t정===============")
        println(promotionItems)
        println("====================================")
        println("총구매액\t\t${totalNumberOfPurchasedItem}\t${String.format("%,d",purchaseAmount)}\n" +
                "행사할인\t\t\t-${String.format("%,d",discountedAmount)}\n" +
                "멤버십할인\t\t\t-${String.format("%,d",membershipDiscount)}\n" +
                "내실돈\t\t\t ${String.format("%,d",totalPurchaseAmount)}")
    }
}