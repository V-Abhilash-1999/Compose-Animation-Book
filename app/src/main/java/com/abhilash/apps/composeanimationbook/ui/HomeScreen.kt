package com.abhilash.apps.composeanimationbook.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abhilash.apps.composeanimationbook.ui.theme.gradientColors

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(Animation.values()) {
            val animationName = it.animationName
            HomeItem(animationName) {
                navController.navigate(animationName)
            }
        }
    }
}

@Composable
private fun HomeItem(
    animationName: String,
    navigate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = {
                navigate()
            },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = gradientColors
                )
            )
        ) {
            Text(text = animationName)
        }
    }
}