package store

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
    println("안녕하세요. Grove1212편의점입니다.\n" +
            "현재 보유하고 있는 상품입니다.\n")
    products.forEach { println(it.toString()) }


}
