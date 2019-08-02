package ru.skillbranch.devintensive.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.graphics.drawable.toDrawable
import ru.skillbranch.devintensive.R

fun Context.convertDpToPx(dp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun Context.convertSpToPx(sp: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

fun Context.getColorAccent(): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
    return typedValue.data
}

fun Context.convertTextToDrawable(text: String): Drawable {
    val width = resources.getDimensionPixelSize(R.dimen.avatar_round_size)
    val height = resources.getDimensionPixelSize(R.dimen.avatar_round_size)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val c = Canvas()
    c.setBitmap(bitmap)

    val halfWidth = (width / 2).toFloat()
    val halfHeight = (height / 2).toFloat()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.style = Paint.Style.FILL
    paint.color = getColorAccent()

    c.drawPaint(paint)

    val bounds = Rect()

    paint.textSize = convertSpToPx(52f)
    paint.color = resources.getColor(android.R.color.white, theme)
    paint.getTextBounds(text, 0, text.length, bounds)

    c.drawText(text, halfWidth - paint.measureText(text) / 2, halfHeight + bounds.height() / 2, paint)

    return bitmap.toDrawable(resources)
}


