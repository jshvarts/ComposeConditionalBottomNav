package com.jshvarts.conditionalbottomnav.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jshvarts.conditionalbottomnav.R
import com.jshvarts.conditionalbottomnav.navigation.CalendarDestinations.CALENDAR_ROUTE
import com.jshvarts.conditionalbottomnav.navigation.ChatDestinations.CHAT_ROUTE
import com.jshvarts.conditionalbottomnav.navigation.HomeDestinations.HOME_ROUTE

enum class BottomBarTab(
  @StringRes val title: Int,
  @DrawableRes val icon: Int,
  val route: String
) {
  HOME(
    R.string.home,
    R.drawable.ic_home,
    "$HOME_GRAPH/$HOME_ROUTE"
  ),
  CALENDAR(
    R.string.calendar,
    R.drawable.ic_calendar_today,
    "$CALENDAR_GRAPH/$CALENDAR_ROUTE"
  ),
  CHAT(
    R.string.chat,
    R.drawable.ic_chat_bubble,
    "$CHAT_GRAPH/$CHAT_ROUTE"
  )
}
