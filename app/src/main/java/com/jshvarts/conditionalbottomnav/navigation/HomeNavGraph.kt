package com.jshvarts.conditionalbottomnav.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jshvarts.conditionalbottomnav.HomeItemDetailScreen
import com.jshvarts.conditionalbottomnav.HomeScreen
import com.jshvarts.conditionalbottomnav.navigation.HomeDestinations.HOME_ITEM_ID_KEY
import com.jshvarts.conditionalbottomnav.navigation.HomeDestinations.HOME_ITEM_ROUTE

const val HOME_GRAPH = "home"

object HomeDestinations {
  const val HOME_ROUTE = "root"
  const val HOME_ITEM_ROUTE = "item"
  const val HOME_ITEM_ID_KEY = "itemId"
}

fun NavGraphBuilder.addHomeGraph(
  onHomeItemSelected: (Int, NavBackStackEntry) -> Unit,
  upPress: () -> Unit,
  modifier: Modifier = Modifier
) {
  composable(BottomBarTab.HOME.route) { from ->
    HomeScreen(onItemClick = { id -> onHomeItemSelected(id, from) }, modifier)
  }
  composable(
    route = "$HOME_ITEM_ROUTE/{${HOME_ITEM_ID_KEY}}",
    arguments = listOf(navArgument(HOME_ITEM_ID_KEY) { type = NavType.IntType })
  ) { backStackEntry ->
    val arguments = requireNotNull(backStackEntry.arguments)
    val itemId = arguments.getInt(HOME_ITEM_ID_KEY)
    HomeItemDetailScreen(itemId, upPress)
  }
}
