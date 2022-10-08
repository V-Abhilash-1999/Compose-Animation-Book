package com.abhilash.apps.composeanimationbook.ui.animationscreen

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import com.abhilash.apps.composeanimationbook.ui.util.dpToPx
import com.abhilash.apps.composeanimationbook.ui.util.toIntSize
import kotlinx.coroutines.launch


@Composable
fun ImageToImageTransition() {
    val imageUris = remember {
        mutableStateOf<Pair<Uri, Uri>?>(null)
    }

    imageUris.value?.let { uriPair ->
        Transition(uriPair)
    } ?: run {
        ImageChooser(imageUris)
    }
}

@Composable
private fun ImageChooser(imageUris: MutableState<Pair<Uri, Uri>?>) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { list ->
        if(list.size >= 2) {
            imageUris.value = list[0] to list[1]
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                launcher.launch("image/*")
            }
        ) {
            Text(text = "Select Two Images")
        }
    }
}

@Composable
private fun Transition(
    imagesUris: Pair<Uri, Uri>
) {
    val point = remember { Animatable(initialValue = 0f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isAnimating = false

    val topImage = imagesUris.second
    var topBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val backgroundImage = imagesUris.first
    var backgroundBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(topImage, backgroundImage) {
        topBitmap = MediaStore
            .Images
            .Media
            .getBitmap(context.contentResolver, topImage)

        backgroundBitmap = MediaStore
            .Images
            .Media
            .getBitmap(context.contentResolver, backgroundImage)
        point.snapTo(0.1f)
        point.snapTo(0f)
    }

    val params = remember(topBitmap, backgroundBitmap) {
        ImageParams(topBitmap, backgroundBitmap, context, point)
    }

    ImageTransitionCanvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                val height = size.height
                detectVerticalDragGestures(
                    onDragEnd = {
                        val progress = point.value / height
                        if (progress > 0.4f) {
                            scope.launch {
                                isAnimating = true
                                point.animateTo(height.toFloat()) {
                                    isAnimating = false
                                }
                            }
                        } else {
                            scope.launch {
                                isAnimating = true
                                point.animateTo(0f) {
                                    isAnimating = false
                                }
                            }
                        }
                    }
                ) { change, dragAmount ->
                    if (!isAnimating) {
                        scope.launch {
                            point.snapTo(point.value + dragAmount)
                        }
                    }
                    change.consume()
                }
            },
        params = params
    )

}

@Composable
fun ImageTransitionCanvas(
    modifier: Modifier = Modifier,
    params: ImageParams
) {
    val context = LocalContext.current
    Canvas(
        modifier = modifier
    ) {

        Log.e("Canvas: ", "Drawing Image and Seoarator")
        drawImages(params)


        //region Separator Line
//        drawSeparator(context, params.point)
        //endregion
    }
}

private fun DrawScope.drawSeparator(
    context: Context,
    point: Animatable<Float, AnimationVector1D>
) {
    val rectSize = Size(size.width, 4.dpToPx(context))
    drawRect(
        color = Color.Red,
        topLeft = Offset(center.x - rectSize.width/2, point.value),
        size = rectSize
    )
}


private fun DrawScope.drawImages(
    params: ImageParams
) {
    Log.e("drawImages: ", "Drawing back And Front")
    params.apply {
        backgroundBitmap?.let {
            drawBottomImage(
                image = it,
                point = point,
                context = context,
                size = size.toIntSize()
            )
        }

        topBitmap?.let {
            drawTopImage(
                image =  it,
                point = point,
                context = context,
                size = size.toIntSize()
            )
        }
    }

}

@Stable
class ImageParams(
    val topBitmap: Bitmap?,
    val backgroundBitmap: Bitmap?,
    val context: Context,
    val point: Animatable<Float, AnimationVector1D>
)

fun DrawScope.drawTopImage(
    image: Bitmap,
    point: Animatable<Float, AnimationVector1D>,
    context: Context,
    size: IntSize
) {
    Log.e("drawTopImage: ", "Drawing Front")
    val progress = point.value / this@drawTopImage.size.height
    val radius = (25 * (1 - progress)).coerceIn(1f, 25f)

    val blurredImage = blurImage(context, image, radius)
    val croppedBitmap = Bitmap.createBitmap(
        blurredImage,
        0,
        0,
        blurredImage.width,
        (blurredImage.height * progress).toInt().coerceIn(1..blurredImage.height)
    )
    drawImage(
        image = croppedBitmap.asImageBitmap(),
        srcSize = size,
        alpha = (progress).calculateAlpha().coerceIn(0f, 1f)
    )
}

fun Float.calculateAlpha(): Float = if(this <= 0.75f) {
    this * (50f/75f)
} else if(this <= 0.875f) {
    this * (75f/87.5f)
} else {
    this
}

fun DrawScope.drawBottomImage(
    image: Bitmap,
    point: Animatable<Float, AnimationVector1D>,
    context: Context,
    size: IntSize
) {
    val progress = point.value / this@drawBottomImage.size.height

    val blurredImage = blurImage(context, image, 25f, progress)
    drawImage(
        image = image.asImageBitmap(), srcSize = IntSize(size.width, size.height)
    )
    if(progress > 0) {
        val croppedBitmap = Bitmap.createBitmap(
            blurredImage,
            0,
            0,
            size.width,
            (size.height * progress).toInt().coerceIn(1..blurredImage.height)
        )
        drawImage(
            croppedBitmap.asImageBitmap(), srcSize =  size
         )
    }

}

fun blurImage(
    context: Context,
    input: Bitmap,
    radius: Float,
    progress: Float = 1f
): Bitmap {
    val rsScript = RenderScript.create(context)
    val scaledHeight = input.getScaledHeight(DisplayMetrics().apply {
        heightPixels = (input.height * progress).toInt().coerceAtLeast(1)
    })
    val inputBitmap = Bitmap.createBitmap(
        input, 0, 0, input.width, scaledHeight
    )
    val alloc = Allocation.createFromBitmap(rsScript, inputBitmap)
    val blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript))
    blur.setRadius(radius)
    blur.setInput(alloc)
    val result = Bitmap.createBitmap(
        input.width,
        input.height,
        Bitmap.Config.ARGB_8888
    )
    val outAlloc = Allocation.createFromBitmap(rsScript, result)
    blur.forEach(outAlloc)
    outAlloc.copyTo(result)
    rsScript.destroy()
    return result
}