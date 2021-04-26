package bazaar.tech.com.basket.model

import com.google.gson.annotations.SerializedName

data class OrderModel(
    @SerializedName("order")
    val order: Order
) {
    data class Order(
        @SerializedName("customer")
        val customer: Customer,
        @SerializedName("financial_status")
        val financialStatus: String, // pending
        @SerializedName("line_items")
        val lineItems: List<LineItem>
    ) {
        data class Customer(
            @SerializedName("id")
            val id: Long // 3007029248045
        )

        data class LineItem(
            @SerializedName("price")
            val price: String, // 20.00
            @SerializedName("quantity")
            val quantity: Int, // 1
            @SerializedName("title")
            val title: String // Product 5
        )
    }
}