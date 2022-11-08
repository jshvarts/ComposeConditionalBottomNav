package com.jshvarts.conditionalbottomnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jshvarts.conditionalbottomnav.navigation.BottomBarTab
import com.jshvarts.conditionalbottomnav.navigation.HOME_GRAPH
import com.jshvarts.conditionalbottomnav.navigation.HomeDestinations.HOME_ITEM_ROUTE
import com.jshvarts.conditionalbottomnav.navigation.navGraph
import com.jshvarts.conditionalbottomnav.ui.theme.ConditionalBottomNavTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ConditionalBottomNavTheme {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()
        DisposableEffect(systemUiController, useDarkIcons) {
          systemUiController.setStatusBarColor(
            color = Color.White,
            darkIcons = useDarkIcons
          )
          onDispose {}
        }

        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          App()
        }
      }
    }
  }
}

@Composable
fun App() {
  ConditionalBottomNavTheme {
    val appState = rememberAppState()
    Scaffold(
      bottomBar = {
        if (appState.shouldShowBottomBar) {
          BottomBar(
            tabs = appState.bottomBarTabs,
            currentRoute = appState.currentRoute!!,
            navigateToRoute = appState::navigateToBottomBarRoute
          )
        }
      }
    ) { innerPaddingModifier ->
      NavHost(
        navController = appState.navController,
        startDestination = HOME_GRAPH,
        modifier = Modifier.padding(innerPaddingModifier)
      ) {
        navGraph(
          onHomeItemSelected = appState::navigateToHomeItemDetail,
          upPress = appState::upPress
        )
      }
    }
  }
}

@Composable
fun rememberAppState(
  navController: NavHostController = rememberNavController()
) =
  remember(navController) {
    AppState(navController)
  }

@Stable
class AppState(
  val navController: NavHostController
) {
  val bottomBarTabs = BottomBarTab.values()
  private val bottomBarRoutes = bottomBarTabs.map { it.route }

  val shouldShowBottomBar: Boolean
    @Composable get() = navController
      .currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes

  val currentRoute: String?
    get() = navController.currentDestination?.route

  fun upPress() {
    navController.navigateUp()
  }

  fun navigateToBottomBarRoute(route: String) {
    if (route != currentRoute) {
      navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(findStartDestination(navController.graph).id) {
          saveState = true
        }
      }
    }
  }

  fun navigateToHomeItemDetail(itemId: Int, from: NavBackStackEntry) {
    // In order to discard duplicated navigation events, we check the Lifecycle
    if (from.lifecycleIsResumed()) {
      navController.navigate("${HOME_ITEM_ROUTE}/$itemId")
    }
  }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
  this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
  get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
  return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}

@Composable
fun BottomBar(
  tabs: Array<BottomBarTab>,
  currentRoute: String,
  navigateToRoute: (String) -> Unit
) {
  BottomNavigation(
    backgroundColor = colorResource(id = R.color.white)
  ) {
    tabs.forEach { item ->
      BottomNavigationItem(
        icon = {
          Icon(
            painter = painterResource(id = item.icon),
            contentDescription = stringResource(id = item.title)
          )
        },
        label = { Text(text = stringResource(id = item.title)) },
        selected = currentRoute == item.route,
        onClick = { navigateToRoute(item.route) }
      )
    }
  }
}
