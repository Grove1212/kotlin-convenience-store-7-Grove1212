package store

import camp.nextstep.edu.missionutils.DateTimes

class Promotion(
    private val name: String,
    private val buy: Int,
    private val get: Int,
    private val startDate: String,
    private val endDate: String
) {
    constructor(line: String) : this(
        name = line.split(",")[0],
        buy = line.split(",")[1].toInt(),
        get = line.split(",")[2].toInt(),
        startDate = line.split(",")[3],
        endDate = line.split(",")[4]
    )

    fun isEqualTo(product: Product) = product.promotion == name
    fun isPromotionOngoing() = DateTimes.now().toString() in startDate..endDate
    fun isEligibleForPromotion(product: Product) = isEqualTo(product) && isPromotionOngoing()

}