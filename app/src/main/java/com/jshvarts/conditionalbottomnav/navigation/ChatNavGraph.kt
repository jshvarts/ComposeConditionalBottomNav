package com.jshvarts.conditionalbottomnav.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jshvarts.conditionalbottomnav.ChatScreen

const val CHAT_GRAPH = "chat"

object ChatDestinations {
  const val CHAT_ROUTE = "root"
}

fun NavGraphBuilder.addChatGraph() {
  composable(BottomBarTab.CHAT.route) {
    ChatScreen()
  }
}
