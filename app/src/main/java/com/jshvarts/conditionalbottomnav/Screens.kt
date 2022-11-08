package com.jshvarts.conditionalbottomnav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
  onItemClick: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val homeItems = remember { HomeItemRepo.getItems() }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(24.dp),
    modifier = modifier
  ) {
    items(homeItems) { item ->
      HomeItemCard(item) {
        onItemClick(it.id)
      }
    }
  }
}

@Composable
fun CalendarScreen() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize()
  ) {
    Text(
      text = stringResource(id = R.string.calendar),
      style = MaterialTheme.typography.h6
    )
  }
}

@Composable
fun ChatScreen() {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize()
  ) {
    Text(
      text = stringResource(id = R.string.chat),
      style = MaterialTheme.typography.h6
    )
  }
}

@Composable
fun HomeItemDetailScreen(
  itemId: Int,
  upPress: () -> Unit
) {
  val item = remember(itemId) { HomeItemRepo.getItem(itemId) }

  Scaffold(
    topBar = {
      TopAppBar(
        backgroundColor = Color.White,
        navigationIcon = {
          IconButton(onClick = upPress) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = stringResource(id = R.string.close)
            )
          }
        },
        title = {
          Text(
            text = item.text,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h6
          )
        }
      )
    }
  ) { innerPaddingModifier ->
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .padding(innerPaddingModifier)
        .fillMaxSize()
    ) {
      Text(
        text = "Home Item $itemId",
        style = MaterialTheme.typography.h6
      )
    }
  }
}

@Composable
fun HomeItemCard(
  item: HomeItem,
  onClick: (HomeItem) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        color = Color.LightGray,
        shape = RoundedCornerShape(4.dp)
      )
      .clickable { onClick(item) }
  ) {
    Text(
      text = item.text,
      style = MaterialTheme.typography.h5,
      modifier = Modifier
        .padding(16.dp)
    )
  }
}
