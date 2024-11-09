package store

class Product(
    private val name: String,
    private val price: Int,
    private val quantity: Int,
    val promotion: String?
) {
    constructor(line: String) : this(
        name = line.split(",")[0],
        price = line.split(",")[1].toInt(),
        quantity = line.split(",")[2].toInt(),
        promotion = line.split(",")[3].let { if (it == "null") null else it }
    )

    private fun checkPayable(count: Int): Boolean = count > quantity
    fun purchase(count: Int): Int {
        require(!checkPayable(count)) {throw IllegalStateException("[ERROR] ${name}의 재고 수량(${quantity})을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.")}
        return  quantity - count
    }
    override fun toString(): String {
        if(quantity == 0){
            return "$name ${String.format("%,d", price)}원 재고 없음"
        }
        return "$name ${String.format("%,d", price)}원 ${quantity}개 ${promotion?.let { "$it" } ?: ""}"
    }
}