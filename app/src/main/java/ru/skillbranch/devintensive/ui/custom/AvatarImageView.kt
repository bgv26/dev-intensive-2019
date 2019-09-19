package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.graphics.toColorInt
import ru.skillbranch.devintensive.R
import kotlin.random.Random

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CircleImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.TRANSPARENT
        private const val DEFAULT_BORDER_WIDTH = 2f
    }

    private var initials: String? = null
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

            typedArray.recycle()
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        initials = null
        super.setImageDrawable(drawable)
    }

    override fun onDraw(canvas: Canvas) {
        if (initials != null) {
            drawAvatar(canvas)
            drawBorder(canvas)
        } else {
            super.onDraw(canvas)
        }
    }

    fun setInitials(initials: String) {
        this.initials = initials
    }

    private fun drawAvatar(canvas: Canvas) {
        val halfSize = (canvas.width / 2).toFloat()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = bgColors[Random.nextInt(bgColors.size)].toColorInt()
        }

        canvas.drawCircle(halfSize, halfSize, halfSize, paint)

        val bounds = Rect()

        with(paint) {
            textSize = halfSize
            color = resources.getColor(android.R.color.white, context.theme)
            getTextBounds(initials, 0, initials!!.length, bounds)
        }

        canvas.drawText(initials!!, halfSize - paint.measureText(initials) / 2, halfSize + bounds.height() / 2, paint)
    }
}

