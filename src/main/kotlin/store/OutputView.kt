package store

class OutputView {
    fun displayProductInfo(products: List<Product>) {
        println(
            "안녕하세요. Grove1212편의점입니다.\n" +
                    "현재 보유하고 있는 상품입니다.\n"
        )
        products.forEach { println(it.toString()) }
    }
}