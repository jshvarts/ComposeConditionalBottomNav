package com.jshvarts.conditionalbottomnav

object HomeItemRepo {
  fun getItems(): List<HomeItem> = getHomeItemList()
  fun getItem(itemId: Int) = getItems().find { it.id == itemId }!!
}

private fun getHomeItemList(): List<HomeItem> {
  return MutableList(20) { index ->
    val cardNumber = index + 1
    HomeItem(cardNumber, "Item $cardNumber")
  }
}

data class HomeItem(
  val id: Int,
  val text: String
)