package com.zfl.launcher.ui.icon

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import com.zfl.launcher.R

/**
 * The bundled iOS 26 icon pack: always on, nothing to install or pick. The PNGs live in
 * `res/drawable-nodpi/ios26_*` and are matched by Android package name. Apps without an entry
 * keep their own icon ([IconLoader] falls back), so a wrong icon is never shown.
 *
 * The artwork is already shaped and shaded, so it is only scaled (aspect ratio kept); it is
 * not re-masked, restyled or given an edge highlight.
 */
object IosIconPack {
    /** Android package name to the pack's drawable. Only exact, known matches are listed. */
    private val byPackage: Map<String, Int> = mapOf(
        "com.google.android.dialer" to R.drawable.ios26_com_apple_mobilephone,
        "com.android.dialer" to R.drawable.ios26_com_apple_mobilephone,
        "com.samsung.android.dialer" to R.drawable.ios26_com_apple_mobilephone,
        "com.android.phone" to R.drawable.ios26_com_apple_mobilephone,
        "com.google.android.contacts" to R.drawable.ios26_com_apple_mobileaddressbook,
        "com.android.contacts" to R.drawable.ios26_com_apple_mobileaddressbook,
        "com.samsung.android.app.contacts" to R.drawable.ios26_com_apple_mobileaddressbook,
        "com.google.android.apps.messaging" to R.drawable.ios26_com_apple_mobilesms,
        "com.android.mms" to R.drawable.ios26_com_apple_mobilesms,
        "com.samsung.android.messaging" to R.drawable.ios26_com_apple_mobilesms,
        "com.android.camera" to R.drawable.ios26_com_apple_camera,
        "com.android.camera2" to R.drawable.ios26_com_apple_camera,
        "com.google.android.GoogleCamera" to R.drawable.ios26_com_apple_camera,
        "com.sec.android.app.camera" to R.drawable.ios26_com_apple_camera,
        "com.mediatek.camera" to R.drawable.ios26_com_apple_camera,
        "com.google.android.gallery3d" to R.drawable.ios26_com_apple_mobileslideshow,
        "com.android.gallery3d" to R.drawable.ios26_com_apple_mobileslideshow,
        "com.sec.android.gallery3d" to R.drawable.ios26_com_apple_mobileslideshow,
        "com.miui.gallery" to R.drawable.ios26_com_apple_mobileslideshow,
        "com.google.android.deskclock" to R.drawable.ios26_com_apple_mobiletimer,
        "com.android.deskclock" to R.drawable.ios26_com_apple_mobiletimer,
        "com.sec.android.app.clockpackage" to R.drawable.ios26_com_apple_mobiletimer,
        "com.google.android.calendar" to R.drawable.ios26_com_apple_mobilecal,
        "com.android.calendar" to R.drawable.ios26_com_apple_mobilecal,
        "com.samsung.android.calendar" to R.drawable.ios26_com_apple_mobilecal,
        "com.google.android.calculator" to R.drawable.ios26_com_apple_calculator,
        "com.android.calculator2" to R.drawable.ios26_com_apple_calculator,
        "com.sec.android.app.popupcalculator" to R.drawable.ios26_com_apple_calculator,
        "com.android.settings" to R.drawable.ios26_com_apple_preferences,
        "com.android.email" to R.drawable.ios26_com_apple_mobilemail,
        "com.samsung.android.email.provider" to R.drawable.ios26_com_apple_mobilemail,
        "com.samsung.android.app.notes" to R.drawable.ios26_com_apple_mobilenotes,
        "com.miui.notes" to R.drawable.ios26_com_apple_mobilenotes,
        "com.google.android.apps.recorder" to R.drawable.ios26_com_apple_voicememos,
        "com.sec.android.app.voicerecorder" to R.drawable.ios26_com_apple_voicememos,
        "com.android.soundrecorder" to R.drawable.ios26_com_apple_voicememos,
        "com.google.android.apps.weather" to R.drawable.ios26_com_apple_weather,
        "com.miui.weather2" to R.drawable.ios26_com_apple_weather,
        "com.google.android.apps.nbu.files" to R.drawable.ios26_com_apple_documentsapp,
        "com.android.documentsui" to R.drawable.ios26_com_apple_documentsapp,
        "com.google.android.documentsui" to R.drawable.ios26_com_apple_documentsapp,
        "com.google.android.apps.walletnfcrel" to R.drawable.ios26_com_apple_passbook,
        "com.google.android.apps.podcasts" to R.drawable.ios26_com_apple_podcasts,
        "com.android.chrome" to R.drawable.ios26_gg_chrome,
        "com.google.android.googlequicksearchbox" to R.drawable.ios26_com_google_googlemobile,
        "com.google.android.apps.photos" to R.drawable.ios26_com_google_photos,
        "com.google.android.gm" to R.drawable.ios26_com_google_gmail,
        "com.google.android.apps.docs" to R.drawable.ios26_com_google_drive,
        "com.google.android.youtube" to R.drawable.ios26_com_google_ios_youtube,
        "com.google.android.apps.youtube.music" to R.drawable.ios26_com_google_ios_youtubemusic,
        "com.google.android.apps.translate" to R.drawable.ios26_com_google_translate,
        "com.google.android.apps.authenticator2" to R.drawable.ios26_com_google_authenticator,
        "com.google.android.apps.bard" to R.drawable.ios26_com_google_gemini,
        "org.telegram.messenger" to R.drawable.ios26_telegram,
        "org.telegram.messenger.web" to R.drawable.ios26_telegram,
        "com.instagram.android" to R.drawable.ios26_instagram,
        "com.instagram.barcelona" to R.drawable.ios26_com_burbn_barcelona,
        "com.facebook.katana" to R.drawable.ios26_facebook,
        "com.facebook.orca" to R.drawable.ios26_com_facebook_messenger,
        "com.discord" to R.drawable.ios26_com_hammerandchisel_discord,
        "com.twitter.android" to R.drawable.ios26_com_atebits_tweetie2,
        "com.reddit.frontpage" to R.drawable.ios26_com_reddit_reddit,
        "com.spotify.music" to R.drawable.ios26_com_spotify_client,
        "com.dropbox.android" to R.drawable.ios26_com_getdropbox_dropbox,
        "nz.mega.android" to R.drawable.ios26_mega,
        "com.github.android" to R.drawable.ios26_github_1,
        "com.adguard.android" to R.drawable.ios26_adguard,
        "com.adguard.android.contentblocker" to R.drawable.ios26_adguard,
        "com.zhiliaoapp.musically" to R.drawable.ios26_com_ss_iphone_ugc_ame,
        "com.ss.android.ugc.trill" to R.drawable.ios26_com_ss_iphone_ugc_ame,
    )

    /** The pack's drawable for [packageName], or `null` if the pack has no icon for it. */
    @DrawableRes
    fun drawableFor(packageName: String): Int? = byPackage[packageName]

    /** Everyday apps in the order an iPhone shows them first; others follow alphabetically. */
    private val homeOrder: List<String> = listOf(
        "com.google.android.calendar", "com.android.calendar", "com.samsung.android.calendar",
        "com.google.android.apps.photos", "com.google.android.gallery3d", "com.android.gallery3d",
        "com.sec.android.gallery3d", "com.miui.gallery",
        "com.android.camera", "com.android.camera2", "com.google.android.GoogleCamera",
        "com.sec.android.app.camera", "com.mediatek.camera",
        "com.google.android.deskclock", "com.android.deskclock", "com.sec.android.app.clockpackage",
        "com.google.android.gm", "com.android.email",
        "com.samsung.android.app.notes", "com.miui.notes",
        "com.google.android.contacts", "com.android.contacts", "com.samsung.android.app.contacts",
        "com.google.android.calculator", "com.android.calculator2", "com.sec.android.app.popupcalculator",
        "com.google.android.apps.nbu.files", "com.android.documentsui", "com.google.android.documentsui",
        "com.android.settings",
    )

    /** Sort key for the first-launch layout: lower comes first, `Int.MAX_VALUE` for apps without a rank. */
    fun homeRank(packageName: String): Int = homeOrder.indexOf(packageName).let { if (it < 0) Int.MAX_VALUE else it }

    /** Number of Android packages that have a pack icon. */
    val size: Int get() = byPackage.size

    /** Renders the pack icon of [packageName] into a [sizePx] square, or `null` without a match or if decoding fails. */
    fun load(resources: Resources, packageName: String, sizePx: Int): ImageBitmap? {
        val id = drawableFor(packageName) ?: return null
        val options = BitmapFactory.Options().apply { inScaled = false }
        val source = BitmapFactory.decodeResource(resources, id, options) ?: return null
        val out = createBitmap(sizePx, sizePx)
        // A few source files are 1 to 5 px off square: fit inside, never stretch.
        val scale = sizePx.toFloat() / maxOf(source.width, source.height)
        val w = source.width * scale
        val h = source.height * scale
        val left = (sizePx - w) / 2f
        val top = (sizePx - h) / 2f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        Canvas(out).drawBitmap(source, null, RectF(left, top, left + w, top + h), paint)
        source.recycle()
        return out.asImageBitmap()
    }
}
