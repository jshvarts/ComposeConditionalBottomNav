package com.jshvarts.conditionalbottomnav.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation

fun NavGraphBuilder.navGraph(
  onHomeItemSelected: (Int, NavBackStackEntry) -> Unit,
  upPress: () -> Unit
) {
  navigation(
    route = HOME_GRAPH,
    startDestination = BottomBarTab.HOME.route
  ) {
    addHomeGraph(onHomeItemSelected, upPress)
    addCalendarGraph()
    addChatGraph()
  }
}
