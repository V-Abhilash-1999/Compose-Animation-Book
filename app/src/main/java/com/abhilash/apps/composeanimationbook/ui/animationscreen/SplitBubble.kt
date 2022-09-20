package com.abhilash.apps.composeanimationbook.ui.animationscreen

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
fun SplitBubble() {
    val scope = rememberCoroutineScope()
    val offsetFromCenter = IntOffset(150.dpToPx, 0.dpToPx)
    val animatable = remember {
        Animatable(0f)
    }
    var horizontalAlignment by  remember { mutableStateOf(1f) }
    val alignmentAnimated = animateFloatAsState(
        targetValue = horizontalAlignment,
        animationSpec = tween(durationMillis = 3000)
    )
    val alignment by animateHorizontalAlignmentAsState(alignmentAnimated.value)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .paddingInDp(32)
                .height(48.dp)
                .graphicsLayer(shape = RoundedCornerShape(50), clip = false)
                .background(Color.Black, RoundedCornerShape(50))
                .clickable {
                    scope.launch {
//                        var animateAlign = false
//                        if(horizontalAlignment == -1f) {
//                            horizontalAlignment = 1f
//                        } else {
//                            animateAlign = true
//                        }

                        if (animatable.targetValue == 0f) {
                            animatable.animateTo(
                                1f,
                                animationSpec = tween(durationMillis = 3000)
                            )
                        } else {
                            animatable.animateTo(
                                0f,
                                animationSpec = tween(durationMillis = 3000)
                            )
                        }

//                        if(animateAlign && horizontalAlignment == 1f) {
//                            horizontalAlignment = -1f
//                        }
                    }
                },
            Alignment.Center,
        ) {
            Text(
                text = "                                     ",
                color = Color.White
            )

            val animatedXValue = (animatable.value * offsetFromCenter.x).toInt()
            val animatedYValue = (animatable.value * offsetFromCenter.y).toInt()

            val offset  = IntOffset(animatedXValue, animatedYValue)

            MovingComposable(
                modifier = Modifier
                    .offset { offset }
                    .align(alignment),
                offset = offset,
                targetOffset = offsetFromCenter
            )
        }
    }
}

@Composable
fun MovingComposable(
    modifier: Modifier,
    offset: IntOffset,
    targetOffset: IntOffset
) {
    val bgColor = Color.Black
    Box(
        modifier = modifier
            .fillMaxHeight()
            .drawBehind {
                val radius = size.height / 2
                val staticTopAngle = 180f
                val staticTopPoint =
                    //Offset(x = size.width / 2 - offset.x, y = 0f - offset.y - 15f)
                    Offset(
                        x = center.x - offset.x + (radius * sin((staticTopAngle * Math.PI).toFloat() / 180F)) - radius,
                        y = center.y - offset.y + (radius * cos((staticTopAngle * Math.PI).toFloat() / 180F))
                    )


                val staticBottomAngle = 0f
                val staticBottomPoint =
                    //Offset(x = size.width / 2 - offset.x, y = size.height - offset.y + 15f)
                    Offset(
                        x = center.x - offset.x + radius * sin((staticBottomAngle * Math.PI).toFloat() / 180F) - radius,
                        y = center.y - offset.y + radius * cos((staticBottomAngle * Math.PI).toFloat() / 180F)
                    )


//                drawCircle(
//                    color = Color.Blue,
//                    center = staticTopPoint,
//                    radius = 10f
//                )
//
//                drawCircle(
//                    color = Color.Blue,
//                    center = staticBottomPoint,
//                    radius = 10f
//                )

                val progress = offset.x.toFloat() / targetOffset.x

                val path = Path()
                val rect = Rect(
                    offset = Offset(
                        -offset.x + 10f,
                        -offset.y + 0f
                    ),
                    size = Size(
                        height = size.height,
                        width = offset.x.toFloat()
                    )
                )
                path.addRect(rect)

//                drawPath(
//                    path = path,
//                    color = Color.Black
//                )

                val staticArcHeight = radius * 2.4f
                val arcHeight = (staticArcHeight * progress * 2).coerceAtMost(staticArcHeight)
                drawCircle(
                    color = bgColor,
                    center = center, //Offset.Zero,
                    radius = radius
                )

                val topAngle = 180f //225f
                val movingTopAngledPoint = Offset(
                    x = center.x + radius * sin((topAngle * Math.PI).toFloat() / 180F),
                    y = center.y + radius * cos((topAngle * Math.PI).toFloat() / 180F)
                )

                val bottomAngle = 0f //315f
                val movingBottomAngledPoint = Offset(
                    x = center.x + radius * sin((bottomAngle * Math.PI).toFloat() / 180F),
                    y = center.y + radius * cos((bottomAngle * Math.PI).toFloat() / 180F)
                )

//                drawCircle(
//                    color = Color.Red,
//                    center = movingTopAngledPoint,
//                    radius = 10f
//                )
//
//                drawCircle(
//                    color = Color.Red,
//                    center = movingBottomAngledPoint,
//                    radius = 10f
//                )

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

//                clipPath(path = path, clipOp = ClipOp.Intersect) {
                    drawPath(
                        path = topPath,
                        color = Color.Black
                    )

                    drawPath(
                        path = bottomPath,
                        color = Color.Black
                    )
//                }
//                drawPath(path, Color.Red)
//                drawPath(topPath, Color.Green)
//                drawPath(bottomPath, Color.Blue)
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


//    drawPath(
//        path = path,
//        color= Color.White,
//        style = Fill,
//    )
    return path
}

@Composable
private fun animateHorizontalAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetBiasValue)
    return derivedStateOf { BiasAlignment(bias, bias) }
}