package hu.bme.aut.android.tesislifebuddy.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import hu.bme.aut.android.tesislifebuddy.model.BarModel

class BarView : View {

    private val paintBg = Paint()
    private val paintOutLine = Paint()
    private val paintBar = Paint()

    private var scale: Float = 1.0F

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        paintBg.color = Color.WHITE
        paintBg.style = Paint.Style.FILL

        paintOutLine.color = Color.BLACK
        paintOutLine.style = Paint.Style.STROKE
        paintOutLine.strokeWidth = 5F

        paintBar.color = Color.RED
        paintBg.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paintBg)
        canvas.drawRect(0F, 0F, width.toFloat() * scale, height.toFloat(), paintBar)
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paintOutLine)
    }

    fun setColor(color: Int) {
        paintBar.color = color
    }

    fun update(new_state: BarModel) {
        scale = new_state.getCurrentValue() / new_state.getMaxValue().toFloat()
        invalidate()
    }
}