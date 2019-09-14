package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import ru.skillbranch.devintensive.R
import kotlin.math.max
import kotlin.random.Random

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CircleImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.TRANSPARENT
        private const val DEFAULT_BORDER_WIDTH = 2f
        private const val DEFAULT_FONT_SIZE = 54f
    }

    private var fontSize = DEFAULT_FONT_SIZE
    private var size = 40
    private val bgColors = arrayOf(
        "#7BC862",
        "#E17076",
        "#FAA774",
        "#6EC9CB",
        "#65AADD",
        "#A695E7",
        "#EE7AAE"
    )

    init {
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.AvatarImageView,
                0,
                0
            )

            mBorderColor = typedArray.getColor(R.styleable.AvatarImageView_borderColor, DEFAULT_BORDER_COLOR)
            mBorderWidth = typedArray.getDimension(R.styleable.AvatarImageView_borderWidth, DEFAULT_BORDER_WIDTH)
            fontSize = typedArray.getDimension(R.styleable.AvatarImageView_fontSize, DEFAULT_FONT_SIZE)

            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("M_AvatarImageView", "onMeasure measuredWidth = $measuredWidth; measuredHeight = $measuredHeight fontSize = $fontSize")
        size = max(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    fun setInitials(initials: String) {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val c = Canvas()
        c.setBitmap(bitmap)

        val halfSize = (size / 2).toFloat()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = bgColors[Random.nextInt(bgColors.size)].toColorInt()
        }

        c.drawPaint(paint)

        val bounds = Rect()

        with(paint) {
            textSize = fontSize
            color = resources.getColor(android.R.color.white, context.theme)
            getTextBounds(initials, 0, initials.length, bounds)
        }

        c.drawText(initials, halfSize - paint.measureText(initials) / 2, halfSize + bounds.height() / 2, paint)

        setImageBitmap(bitmap)
    }
}