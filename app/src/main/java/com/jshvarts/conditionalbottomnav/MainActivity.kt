package com.jshvarts.conditionalbottomnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.jshvarts.conditionalbottomnav.MainDestinations.GAME_CARD_DETAIL_ROUTE
import com.jshvarts.conditionalbottomnav.MainDestinations.HOME_ROUTE
import com.jshvarts.conditionalbottomnav.ui.theme.ConditionalBottomNavTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ConditionalBottomNavTheme {
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
    ) {
      NavHost(
        navController = appState.navController,
        startDestination = MainDestinations.HOME_ROUTE
      ) {
        navGraph(appState.navController)
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

fun NavGraphBuilder.navGraph(navController: NavController) {
  navigation(
    route = HOME_ROUTE,
    startDestination = BottomBarTab.CATALOG.route
  ) {
    addHomeGraph(navController)
  }
}

fun NavGraphBuilder.addHomeGraph(
  navController: NavController,
  modifier: Modifier = Modifier
) {
  composable(BottomBarTab.CATALOG.route) {
    CatalogScreen(navController)
  }
  composable(BottomBarTab.PROFILE.route) {
    ProfileScreen()
  }
  composable(BottomBarTab.SEARCH.route) {
    SearchScreen()
  }
  composable(GAME_CARD_DETAIL_ROUTE) {
    MyCard()
  }
}

object MainDestinations {
  const val HOME_ROUTE = "home"
  const val GAME_CARD_DETAIL_ROUTE = "cardRoute"
}

enum class BottomBarTab(
  @StringRes val title: Int,
  val icon: ImageVector,
  val route: String
) {
  CATALOG(R.string.home_catalog, Icons.Outlined.Home, "$HOME_ROUTE/catalog"),
  PROFILE(R.string.home_profile, Icons.Outlined.AccountCircle, "$HOME_ROUTE/profile"),
  SEARCH(R.string.home_search, Icons.Outlined.Search, "$HOME_ROUTE/search")
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
  navigateToRoute: (String) -> Unit,
  color: Color = Color.White,
  contentColor: Color = Color.Black
) {
//  val routes = remember { tabs.map { it.route } }
//  val currentSection = tabs.first { it.route == currentRoute }
  Surface(
    color = color,
    contentColor = contentColor
  ) {
    BottomNavigation {
      tabs.forEach { item ->
        BottomNavigationItem(
          icon = {
            Icon(
              imageVector = item.icon,
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
}

@Composable
fun CatalogScreen(navController: NavController) {
  Text(
    text = "catalog",
    modifier = Modifier
      .clickable {
        navController.navigate(GAME_CARD_DETAIL_ROUTE)
      })
}

@Composable
fun ProfileScreen() {
  Text("profile")
}

@Composable
fun SearchScreen() {
  Text("search")
}

@Composable
fun MyCard() {
  Text("my card")
}

@Preview
@Composable
private fun bottomNavPreview() {
  ConditionalBottomNavTheme {
    BottomBar(
      tabs = BottomBarTab.values(),
      currentRoute = "home/catalog",
      navigateToRoute = { }
    )
  }
}