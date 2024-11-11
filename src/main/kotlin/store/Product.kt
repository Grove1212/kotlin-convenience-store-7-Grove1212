package store

class Product(
    val name: String,
    private val price: Int,
    var quantity: Int,
    val promotion: String?
) {
    constructor(line: String) : this(
        name = line.split(",")[0],
        price = line.split(",")[1].toInt(),
        quantity = line.split(",")[2].toInt(),
        promotion = line.split(",")[3].let { if (it == "null") null else it }
    )

    fun isPromotionalProduct(name: String) = (this.name == name && this.promotion != null && quantity != 0)
    fun isNonPromotionalProduct(name: String) = (this.name == name && this.promotion == null)

    fun countLeftQuantity(count: Int): Int {
        return quantity-count
    }

    fun decreaseStock(count: Int) {
        quantity -= count
    }

    fun calculatePurchasedAmount(count: Int): Int {
        return price * count
    }

    override fun toString(): String {
        if (quantity == 0) {
            return "- $name ${String.format("%,d", price)}원 재고 없음"
        }
        return "- $name ${String.format("%,d", price)}원 ${quantity}개 ${promotion?: ""}"
    }
}