package store

class Product(
    private val name: String,
    private val price: Int,
    private val quantity: Int,
    private val promotion: String?
) {
    constructor(line: String) : this(
        name = line.split(",")[0],
        price = line.split(",")[1].toInt(),
        quantity = line.split(",")[2].toInt(),
        promotion = line.split(",")[3].let { if (it == "null") null else it }
    )

    fun checkPayable(count: Int): Boolean = count > quantity

}