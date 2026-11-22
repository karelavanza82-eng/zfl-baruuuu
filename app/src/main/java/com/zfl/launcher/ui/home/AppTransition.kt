package com.zfl.launcher.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.lerp as lerpRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zfl.launcher.LauncherViewModel
import com.zfl.launcher.ui.theme.Motion
import kotlin.math.roundToInt

enum class AppTransitionPhase { Opening, Closing }

/**
 * An app opening out of its icon, or closing back into it. [bounds] is the icon's rectangle in
 * window pixels. The launcher draws the surface itself ([AppTransitionOverlay]); the app's own
 * window animation is a short fade, because a launcher cannot drive another app's window.
 */
@Immutable
data class AppTransition(
    val id: Long,
    val phase: AppTransitionPhase,
    val bounds: Rect,
    val icon: ImageBitmap?,
    val color: Color,
)

/** Average color of [icon], slightly darkened: the color of the surface that grows from it. */
fun iconSurfaceColor(icon: ImageBitmap?): Color {
    val bitmap = icon?.asAndroidBitmap() ?: return Color.Black
    return runCatching {
        val pixel = android.graphics.Bitmap.createScaledBitmap(bitmap, 1, 1, true).getPixel(0, 0)
        androidx.compose.ui.graphics.lerp(Color(pixel).copy(alpha = 1f), Color.Black, 0.25f)
    }.getOrDefault(Color.Black)
}

/**
 * Draws the surface between an icon and the full screen. [progress] is 0 at the icon and 1 at
 * full screen; the home screen reads it too, to shrink and blur behind the surface.
 */
@Composable
fun AppTransitionOverlay(vm: LauncherViewModel, progress: Animatable<Float, AnimationVector1D>) {
    val current by vm.appTransition.collectAsStateWithLifecycle()
    val transition = current
    LaunchedEffect(transition?.id) {
        val t = transition ?: return@LaunchedEffect
        when (t.phase) {
            AppTransitionPhase.Opening -> {
                progress.snapTo(0f)
                progress.animateTo(1f, tween(Motion.APP_OPEN_MS, easing = Motion.IosEasing))
            }
            AppTransitionPhase.Closing -> {
                progress.animateTo(0f, tween(Motion.APP_CLOSE_MS, easing = Motion.IosEasing))
                vm.endAppTransition(t.id)
            }
        }
    }
    if (transition == null) return
    Canvas(Modifier.fillMaxSize()) { drawSurface(transition, progress.value) }
}

private fun DrawScope.drawSurface(t: AppTransition, p: Float) {
    val rect = lerpRect(t.bounds, Rect(Offset.Zero, size), p)
    val iconCorner = t.bounds.width * ICON_CORNER_RATIO
    val corner = lerp(iconCorner, Motion.APP_WINDOW_CORNER_DP * density, p)
    // Opaque from the first moments, so the icon's own pixels underneath never show through.
    drawRoundRect(
        color = t.color.copy(alpha = (p * 5f).coerceIn(0f, 1f)),
        topLeft = rect.topLeft,
        size = rect.size,
        cornerRadius = CornerRadius(corner),
    )
    val icon = t.icon ?: return
    // The icon sits over the icon's own spot at p = 0, so it never visibly jumps, then fades.
    val side = lerp(t.bounds.width, t.bounds.width * ICON_END_SCALE, p)
    val center = rect.center
    drawImage(
        image = icon,
        dstOffset = IntOffset((center.x - side / 2f).roundToInt(), (center.y - side / 2f).roundToInt()),
        dstSize = IntSize(side.roundToInt(), side.roundToInt()),
        alpha = (1f - p * 2.5f).coerceIn(0f, 1f),
    )
}

/** Corner of an icon as a share of its side, like the squircle of the home icons. */
private const val ICON_CORNER_RATIO = 0.2237f
private const val ICON_END_SCALE = 1.25f
