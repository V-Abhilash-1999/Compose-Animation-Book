package com.abhilash.apps.composeanimationbook.ui.animationscreen

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.abhilash.apps.composeanimationbook.ui.util.dpToPx
import com.abhilash.apps.composeanimationbook.ui.util.paddingInDp
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun Mitosis() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val offsetFromCenter = remember { mutableStateListOf<IntOffset>() }
    val mitosisDrop = remember { mutableStateListOf<Animatable<Float, AnimationVector1D>>() }
    val mitosisAlignment = remember { mutableStateListOf<Animatable<Float, AnimationVector1D>>() }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .paddingInDp(32)
                .size(48.dp)
                .graphicsLayer(shape = RoundedCornerShape(50), clip = false)
                .background(Color.Black, RoundedCornerShape(50))
                .align(Alignment.Center)
                .clickable {
                    val index = offsetFromCenter.size
                    mitosisDrop.add(Animatable(0f))
                    mitosisAlignment.add(Animatable(0f))
                    offsetFromCenter.add(
                        IntOffset(
                            x = 150.dpToPx(context).toInt(),
                            y = 0
                        )
                    )
                    scope.launch {
                        val animSpec = tween<Float>(durationMillis = 3000)
                        val onAnimationEnd: Animatable<Float, AnimationVector1D>.() -> Unit = {
                            if (value == targetValue) {
                                launch {
                                    if (mitosisDrop[index].targetValue == 0f) {
                                        mitosisDrop[index].animateTo(
                                            1f,
                                            initialVelocity = 100f,
                                            animationSpec = animSpec
                                        )
                                    }
                                }
                            }
                        }

                        if (mitosisAlignment[index].targetValue == 0f) {
                            mitosisAlignment[index].animateTo(
                                1f,
                                animationSpec = tween(
                                    durationMillis = 1000,
                                    easing = FastOutSlowInEasing
                                ),
                                block = onAnimationEnd
                            )
                        }
                    }
                },
            Alignment.Center,
        ) {
            offsetFromCenter.forEachIndexed { index, intOffset ->
                MitosisDrop(
                    mitosisDrop = mitosisDrop[index],
                    alignment = animateHorizontalAlignmentAsState(targetBiasValue = mitosisAlignment[index].value).value,
                    offsetFromCenter = intOffset
                )
            }
        }
    }
}

@Composable
private fun BoxScope.MitosisDrop(
    mitosisDrop: Animatable<Float, AnimationVector1D>,
    alignment: Alignment,
    offsetFromCenter: IntOffset
) {

    val animatedXValue = (mitosisDrop.value * offsetFromCenter.x).toInt()
    val animatedYValue = (mitosisDrop.value * offsetFromCenter.y).toInt()

    val offset  = remember(animatedXValue, animatedYValue) {
        IntOffset(animatedXValue, animatedYValue)
    }

    MovingComposable(
        modifier = Modifier
            .offset { offset }
            .align(alignment),
        offset = offset,
        targetOffset = offsetFromCenter
    )
}

@Composable
private fun MovingComposable(
    modifier: Modifier,
    offset: IntOffset,
    targetOffset: IntOffset
) {
    val bgColor = Color.Black
    Box(
        modifier = modifier
            .fillMaxHeight()
            .drawBehind {
                val staticAngledY = targetOffset.y + 90f


                val radius = size.height / 2
                val staticTopAngle = staticAngledY + 90
                val staticTopPoint =
                    Offset(
                        x = center.x - offset.x + (radius * sin((staticTopAngle * Math.PI).toFloat() / 180F)) - radius,
                        y = center.y - offset.y + (radius * cos((staticTopAngle * Math.PI).toFloat() / 180F))
                    )


                val staticBottomAngle = staticAngledY - 90
                val staticBottomPoint =
                    Offset(
                        x = center.x - offset.x + radius * sin((staticBottomAngle * Math.PI).toFloat() / 180F) - radius,
                        y = center.y - offset.y + radius * cos((staticBottomAngle * Math.PI).toFloat() / 180F)
                    )


                val progress = offset.x.toFloat() / targetOffset.x

                val path = Path()
                val rect = Rect(
                    offset = Offset(
                        -offset.x + 10f,
                        -offset.y + targetOffset.y.toFloat()
                    ),
                    size = Size(
                        height = size.height,
                        width = offset.x.toFloat()
                    )
                )
                path.addRect(rect)

                val staticArcHeight = radius * 2.4f
                val arcHeight = (staticArcHeight * progress * 2).coerceAtMost(staticArcHeight)
                drawCircle(
                    color = bgColor,
                    center = center,
                    radius = radius
                )


                val angledY = offset.y + 90f

                val topAngle = angledY + 90f
                val movingTopAngledPoint = Offset(
                    x = center.x + radius * sin((topAngle * Math.PI).toFloat() / 180F),
                    y = center.y + radius * cos((topAngle * Math.PI).toFloat() / 180F)
                )

                val bottomAngle = angledY - 90f
                val movingBottomAngledPoint = Offset(
                    x = center.x + radius * sin((bottomAngle * Math.PI).toFloat() / 180F),
                    y = center.y + radius * cos((bottomAngle * Math.PI).toFloat() / 180F)
                )

                val topPath = drawCustomArc(
                    x1 = staticTopPoint.x,
                    y1 = staticTopPoint.y,
                    x2 = movingTopAngledPoint.x,
                    y2 = movingTopAngledPoint.y,
                    curveRadius = arcHeight,
                    sweepAngle = 90f
                )

                val bottomPath = drawCustomArc(
                    x1 = staticBottomPoint.x,
                    y1 = staticBottomPoint.y,
                    x2 = movingBottomAngledPoint.x,
                    y2 = movingBottomAngledPoint.y,
                    curveRadius = arcHeight,
                    sweepAngle = -90f
                )

                if (progress < 0.5f) {
                    clipPath(path = topPath, clipOp = ClipOp.Difference) {
                        clipPath(path = bottomPath, clipOp = ClipOp.Difference) {
                            drawPath(
                                path = path,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = " ",
            color = Color.Green,
            textAlign = TextAlign.Center
        )
    }
}

private fun DrawScope.drawCustomArc(
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
    curveRadius: Float,
    sweepAngle: Float
): Path {
    val path = Path()
    val midX = x1 + (x2 - x1) / 2
    val midY = y1 + (y2 - y1) / 2
    val xDiff: Float = midX - x1
    val yDiff: Float = midY - y1
    val angle = atan2(yDiff.toDouble(), xDiff.toDouble()) * (180 / Math.PI) + sweepAngle
    val angleRadians = Math.toRadians(angle)
    val pointX = (midX + curveRadius * cos(angleRadians)).toFloat()
    val pointY = (midY + curveRadius * sin(angleRadians)).toFloat()

    path.moveTo(x1, y1)
    path.cubicTo(x1, y1, pointX, pointY, x2, y2)

    return path
}

@Composable
private fun animateHorizontalAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetBiasValue)
    return remember { derivedStateOf { BiasAlignment(bias, bias) } }
}