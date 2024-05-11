data class Item(
    val imageResource: Int,
    val title: String,
    val price: Double
) {
    companion object {
        var lastId = 0
    }

    val id: Int

    init {
        id = lastId++
    }
}