package store

class PurchaseOrder(
    val OrderInformation: List<Triple<Int, Product, Promotion>>,
    val freebies: List<Pair<Int, Product>>

) {
    fun getTotalNumberOfProduct(): Int {
        return 0
    }
}