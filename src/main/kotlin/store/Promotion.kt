package store

class Promotion(
    private val name: String,
    private val buy: Int,
    private val get: Int,
    private val start_date: String,
    private val end_date: String
) {
    constructor(line: String) : this(
        name = line.split(",")[0],
        buy = line.split(",")[1].toInt(),
        get = line.split(",")[1].toInt(),
        start_date = line.split(",")[2],
        end_date = line.split(",")[3]
    )

    fun isEligibleForPromotion(product: Product) = product.promotion == name

}