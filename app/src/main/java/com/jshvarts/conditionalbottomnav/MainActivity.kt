package com.jshvarts.conditionalbottomnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
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
      },
      scaffoldState = appState.scaffoldState
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
  scaffoldState: ScaffoldState = rememberScaffoldState(),
  navController: NavHostController = rememberNavController()
) =
  remember(scaffoldState, navController) {
    AppState(scaffoldState, navController)
  }

fun NavGraphBuilder.navGraph(navController: NavController) {
  navigation(
    route = MainDestinations.HOME_ROUTE,
    startDestination = HomeSections.CATALOG.route
  ) {
    addHomeGraph(navController)
  }
}

fun NavGraphBuilder.addHomeGraph(
  navController: NavController,
  modifier: Modifier = Modifier
) {
  composable(HomeSections.CATALOG.route) {
    CatalogScreen(navController)
  }
  composable(HomeSections.PROFILE.route) {
    ProfileScreen()
  }
  composable(HomeSections.SEARCH.route) {
    SearchScreen()
  }
  composable(MainDestinations.GAME_CARD_DETAIL_ROUTE) {
    MyCard()
  }
}

object MainDestinations {
  const val HOME_ROUTE = "home"
  const val GAME_CARD_DETAIL_ROUTE = "cardRoute"
  const val GAME_CARD = "gameCard"
  const val SUB_CATALOG_ROUTE = "subCatalog"
  const val CATALOG_GAME = "catalogGame"
}

enum class HomeSections(
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
  val scaffoldState: ScaffoldState,
  val navController: NavHostController
) {
  // ----------------------------------------------------------
  // Источник состояния BottomBar
  // ----------------------------------------------------------

  val bottomBarTabs = HomeSections.values()
  private val bottomBarRoutes = bottomBarTabs.map { it.route }

  // Атрибут отображения навигационного меню bottomBar
  val shouldShowBottomBar: Boolean
    @Composable get() = navController
      .currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes

  // ----------------------------------------------------------
  // Источник состояния навигации
  // ----------------------------------------------------------

  val currentRoute: String?
    get() = navController.currentDestination?.route

  fun upPress() {
    navController.navigateUp()
  }

  // Клик по навигационному меню, вкладке.
  fun navigateToBottomBarRoute(route: String) {
    if (route != currentRoute) {
      navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
        //Возвращаем выбранный экран,
        //иначе если backstack не пустой то показываем ранее открытое состяние
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
  tabs: Array<HomeSections>,
  currentRoute: String,
  navigateToRoute: (String) -> Unit,
  color: Color = Color.White,
  contentColor: Color = Color.Black
) {
  val routes = remember { tabs.map { it.route } }
  val currentSection = tabs.first { it.route == currentRoute }

  Surface(
    color = color,
    contentColor = contentColor
  ) {
    val springSpec = SpringSpec<Float>(
      stiffness = 800f,
      dampingRatio = 0.8f
    )
    bottomNavLayout(
      selectedIndex = currentSection.ordinal,
      itemCount = routes.size,
      indicator = { bottomNavIndicator() },
      animSpec = springSpec,
      //modifier = Modifier.navigationBarsPadding(start = false, end = false)
    ) {
      tabs.forEach { section ->
        val selected = section == currentSection
        val tint by animateColorAsState(
          if (selected) {
            Color.Black
          } else {
            Color.Gray
          }
        )

        BottomNavigationItem(
          icon = {
            Icon(
              imageVector = section.icon,
              tint = tint,
              contentDescription = null
            )
          },
          text = {
            Text(
              text = stringResource(section.title).uppercase(
                ConfigurationCompat.getLocales(
                  LocalConfiguration.current
                ).get(0)!!
              ),
              color = tint,
              style = MaterialTheme.typography.button,
              maxLines = 1
            )
          },
          selected = selected,
          onSelected = { navigateToRoute(section.route) },
          animSpec = springSpec,
          modifier = BottomNavigationItemPadding
            .clip(BottomNavIndicatorShape)
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

@Composable
private fun bottomNavLayout(
  selectedIndex: Int,
  itemCount: Int,
  animSpec: AnimationSpec<Float>,
  indicator: @Composable BoxScope.() -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  // Track how "selected" each item is [0, 1]
  val selectionFractions = remember(itemCount) {
    List(itemCount) { i ->
      Animatable(if (i == selectedIndex) 1f else 0f)
    }
  }
  selectionFractions.forEachIndexed { index, selectionFraction ->
    val target = if (index == selectedIndex) 1f else 0f
    LaunchedEffect(target, animSpec) {
      selectionFraction.animateTo(target, animSpec)
    }
  }

  // Animate the position of the indicator
  val indicatorIndex = remember { Animatable(0f) }
  val targetIndicatorIndex = selectedIndex.toFloat()
  LaunchedEffect(targetIndicatorIndex) {
    indicatorIndex.animateTo(targetIndicatorIndex, animSpec)
  }

  Layout(
    modifier = modifier.height(BottomNavHeight),
    content = {
      content()
      Box(Modifier.layoutId("indicator"), content = indicator)
    }
  ) { measurables, constraints ->
    check(itemCount == (measurables.size - 1)) // account for indicator

    // Divide the width into n+1 slots and give the selected item 2 slots
    val unselectedWidth = constraints.maxWidth / (itemCount + 1)
    val selectedWidth = 2 * unselectedWidth
    val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

    val itemPlaceables = measurables
      .filterNot { it == indicatorMeasurable }
      .mapIndexed { index, measurable ->
        // Animate item's width based upon the selection amount
        val width = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
        measurable.measure(
          constraints.copy(
            minWidth = width,
            maxWidth = width
          )
        )
      }
    val indicatorPlaceable = indicatorMeasurable.measure(
      constraints.copy(
        minWidth = selectedWidth,
        maxWidth = selectedWidth
      )
    )

    layout(
      width = constraints.maxWidth,
      height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
    ) {
      val indicatorLeft = indicatorIndex.value * unselectedWidth
      indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
      var x = 0
      itemPlaceables.forEach { placeable ->
        placeable.placeRelative(x = x, y = 0)
        x += placeable.width
      }
    }
  }
}

@Composable
fun BottomNavigationItem(
  icon: @Composable BoxScope.() -> Unit,
  text: @Composable BoxScope.() -> Unit,
  selected: Boolean,
  onSelected: () -> Unit,
  animSpec: AnimationSpec<Float>,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.selectable(selected = selected, onClick = onSelected),
    contentAlignment = Alignment.Center
  ) {
    // Animate the icon/text positions within the item based on selection
    val animationProgress by animateFloatAsState(if (selected) 1f else 0f, animSpec)
    bottomNavItemLayout(
      icon = icon,
      text = text,
      animationProgress = animationProgress
    )
  }
}

@Composable
private fun bottomNavItemLayout(
  icon: @Composable BoxScope.() -> Unit,
  text: @Composable BoxScope.() -> Unit,
  @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
) {
  Layout(
    content = {
      Box(
        modifier = Modifier
          .layoutId("icon")
          .padding(horizontal = TextIconSpacing),
        content = icon
      )
      val scale = lerp(0.6f, 1f, animationProgress)
      Box(
        modifier = Modifier
          .layoutId("text")
          .padding(horizontal = TextIconSpacing)
          .graphicsLayer {
            alpha = animationProgress
            scaleX = scale
            scaleY = scale
            transformOrigin = BottomNavLabelTransformOrigin
          },
        content = text
      )
    }
  ) { measurables, constraints ->
    val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
    val textPlaceable = measurables.first { it.layoutId == "text" }.measure(constraints)

    placeTextAndIcon(
      textPlaceable,
      iconPlaceable,
      constraints.maxWidth,
      constraints.maxHeight,
      animationProgress
    )
  }
}

private fun MeasureScope.placeTextAndIcon(
  textPlaceable: Placeable,
  iconPlaceable: Placeable,
  width: Int,
  height: Int,
  @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
  val iconY = (height - iconPlaceable.height) / 2
  val textY = (height - textPlaceable.height) / 2

  val textWidth = textPlaceable.width * animationProgress
  val iconX = (width - textWidth - iconPlaceable.width) / 2
  val textX = iconX + iconPlaceable.width

  return layout(width, height) {
    iconPlaceable.placeRelative(iconX.toInt(), iconY)
    if (animationProgress != 0f) {
      textPlaceable.placeRelative(textX.toInt(), textY)
    }
  }
}

@Composable
private fun bottomNavIndicator(
  strokeWidth: Dp = 2.dp,
  color: Color = Color.Black,
  shape: Shape = BottomNavIndicatorShape
) {
  Spacer(
    modifier = Modifier
      .fillMaxSize()
      .then(BottomNavigationItemPadding)
      .border(strokeWidth, color, shape)
  )
}

private val TextIconSpacing = 2.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Preview
@Composable
private fun bottomNavPreview() {
  ConditionalBottomNavTheme {
    BottomBar(
      tabs = HomeSections.values(),
      currentRoute = "home/catalog",
      navigateToRoute = { }
    )
  }
}