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
    fun purchase(count: Int): Int {
        require(!checkPayable(count)) {throw IllegalArgumentException("[ERROR] 재고 부족으로 주문 불가합니다. 재고: ${quantity}")}
        return  quantity - count
    }
}