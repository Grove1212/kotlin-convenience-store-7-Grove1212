package store

import java.io.File

fun main() {
    // TODO: 프로그램 구현
    //1. 파일 가져오기
    val PRODUCTSPATH = "/src/main/kotlin/resources/products.md"
    val PROMOTIONSPATH = "/src/main/kotlin/resources/promotions.md"

    //2. product, promotion 객체 생성
    val products = File(PRODUCTSPATH).useLines { lines ->
        lines.map { Product(it) }.toList()
    }
    val promotions = File(PROMOTIONSPATH).useLines { lines ->
        lines.map { Promotion(it) }.toList()
    }



}
