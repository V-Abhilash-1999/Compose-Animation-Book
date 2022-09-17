package com.abhilash.apps.composeanimationbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhilash.apps.composeanimationbook.ui.Animation
import com.abhilash.apps.composeanimationbook.ui.HomeScreen
import com.abhilash.apps.composeanimationbook.ui.theme.ComposeAnimationBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeAnimationBookTheme {
                AnimationNavigation()
            }
        }
    }
}

@Composable
private fun AnimationNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, "HOME_SCREEN") {
        composable("HOME_SCREEN") {
            HomeScreen(navController = navController)
        }
        Animation.values().forEach { animation ->
            composable(animation.animationName) {
                when(animation) {
                    Animation.SPLIT_BUBBLE -> {

                    }
                    else -> {
                        EmptyScreen(animationName = animation.animationName)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyScreen(animationName: String) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Animation $animationName has not been added Yet",
            color = MaterialTheme.colorScheme.error
        )

    }
}