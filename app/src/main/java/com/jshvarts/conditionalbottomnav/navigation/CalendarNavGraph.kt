package com.jshvarts.conditionalbottomnav.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jshvarts.conditionalbottomnav.CalendarScreen

const val CALENDAR_GRAPH = "calendar"

object CalendarDestinations {
  const val CALENDAR_ROUTE = "root"
}

fun NavGraphBuilder.addCalendarGraph() {
  composable(BottomBarTab.CALENDAR.route) {
    CalendarScreen()
  }
}
