package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.roundToInt

open class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2f
    }

    private var mBitmapShader: BitmapShader? = null
    private var mBitmap: Bitmap? = null
    private var mBorderBounds: RectF
    private var mBitmapBounds: RectF
    private var mBorderPaint: Paint
    private var mBitmapPaint: Paint
    private var mShaderMatrix: Matrix
    internal var mBorderColor = DEFAULT_BORDER_COLOR
    internal var mBorderWidth = DEFAULT_BORDER_WIDTH

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, 0, 0)

            mBorderColor = typedArray.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            mBorderWidth = typedArray.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)

            typedArray.recycle()
        }

        mShaderMatrix = Matrix()
        mBorderBounds = RectF()
        mBitmapBounds = RectF()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        setupBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        super.setImageBitmap(bitmap)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        updateCircleDrawBounds(mBitmapBounds)
        mBorderBounds.set(mBitmapBounds)
        mBorderBounds.inset(mBorderWidth, mBorderWidth)

        updateBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawBorder(canvas)
    }

    @Dimension
    fun getBorderWidth(): Int = mBorderWidth.roundToInt()

    fun setBorderWidth(@Dimension dp: Int) {
        mBorderWidth = dp.toFloat()

        updateBitmap()
    }

    fun getBorderColor(): Int = mBorderColor

    fun setBorderColor(hex: String) {
        mBorderColor = Color.parseColor(hex)

        updateBitmap()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        mBorderColor = resources.getColor(colorId, context.theme)
    }

    internal fun drawBorder(canvas: Canvas) {
        if (mBorderWidth > 0f) {
            with(mBorderPaint) {
                color = mBorderColor
                style = Paint.Style.STROKE
                strokeWidth = mBorderWidth
            }
            canvas.drawOval(mBorderBounds, mBorderPaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapBounds, mBitmapPaint)
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val diameter = min(contentWidth, contentHeight)

        val left = paddingLeft + (contentWidth - diameter) / 2f
        val top = paddingTop + (contentHeight - diameter) / 2f

        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        scaleType = ScaleType.CENTER_CROP

        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmap()
    }

    private fun updateBitmap() {
        if (mBitmap == null) return

        val dx: Float
        val dy: Float
        val scale: Float

        // scale up/down with respect to this view size and maintain aspect ratio
        // translate bitmap position with dx/dy to the center of the image
        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapBounds.width() / mBitmap!!.width
            dx = mBitmapBounds.left
            dy = mBitmapBounds.top - mBitmap!!.height * scale / 2f + mBitmapBounds.height() / 2f
        } else {
            scale = mBitmapBounds.height() / mBitmap!!.height
            dx = mBitmapBounds.left - mBitmap!!.width * scale / 2f + mBitmapBounds.width() / 2f
            dy = mBitmapBounds.top
        }

        with(mShaderMatrix) {
            setScale(scale, scale)
            mShaderMatrix.postTranslate(dx, dy)
        }
        mBitmapShader?.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? = when (drawable) {
        null -> null

        is BitmapDrawable -> drawable.bitmap

        else -> {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            bitmap
        }
    }
}