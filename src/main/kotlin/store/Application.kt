package store

import camp.nextstep.edu.missionutils.Console

fun main() {
    // TODO: 프로그램 구현

    val inputView = InputView()
    val outputView = OutputView()
    val cashRegister = CashRegister(inputView.loadProducts(), inputView.loadPromotions(), inputView, outputView)
    cashRegister.run()
}
