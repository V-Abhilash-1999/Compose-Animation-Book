package com.abhilash.apps.composeanimationbook.ui.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun Modifier.paddingInDp(size: Int) = this.padding(size.dp)

val Int.dpToPx : Int
    @Composable
    get() {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

fun Int.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}


fun Size.toIntSize() = IntSize(width.toInt(), height.toInt())