package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import ru.skillbranch.devintensive.R
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_STROKE_COLOR = Color.TRANSPARENT
        private const val DEFAULT_STROKE_WIDTH = 0
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val SCALE_TYPE = ScaleType.CENTER_CROP
    }

    private var mBitmapShader: BitmapShader? = null
    private var mBitmap: Bitmap? = null
    private var mInitialized: Boolean
    private var mStrokeBounds: RectF
    private var mBitmapDrawBounds: RectF
    private var mStrokePaint: Paint
    private var mBitmapPaint: Paint
    private var mShaderMatrix: Matrix
    private var mStrokeColor = DEFAULT_STROKE_COLOR
    private var mStrokeWidth = DEFAULT_STROKE_WIDTH

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, 0, 0)

            mStrokeColor = a.getColor(R.styleable.CircleImageView_strokeColor, DEFAULT_STROKE_COLOR)
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_strokeWidth, DEFAULT_STROKE_WIDTH)

            a.recycle()
        }

        mShaderMatrix = Matrix()
        mStrokeBounds = RectF()
        mBitmapDrawBounds = RectF()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint.color = mStrokeColor
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeWidth = mStrokeWidth.toFloat()

        mInitialized = true

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

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = mStrokePaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mStrokeBounds.set(mBitmapDrawBounds)
        mStrokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawStroke(canvas)
    }

    private fun drawStroke(canvas: Canvas) {
        if (mStrokePaint.strokeWidth > 0f) {
            canvas.drawOval(mStrokeBounds, mStrokePaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        super.setScaleType(SCALE_TYPE)

        if (!mInitialized) {
            return
        }
        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        if (mBitmap == null) return

        val dx: Float
        val dy: Float
        val scale: Float

        // scale up/down with respect to this view size and maintain aspect ratio
        // translate bitmap position with dx/dy to the center of the image
        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width
            dx = mBitmapDrawBounds.left
            dy = mBitmapDrawBounds.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height
            dx = mBitmapDrawBounds.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader?.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            BITMAP_CONFIG
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}