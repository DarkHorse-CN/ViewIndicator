package com.darkhorse.viewindicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.TextView

@SuppressLint("ViewConstructor")
/**
 * Description:
 * Created by DarkHorse on 2018/6/29.
 */
class ColorTrackTextView(context: Context, colorNormal: Int, colorSelector: Int, textSize: Int, width: Int, text: String) : TextView(context) {

    private var mTitleColorNormal: Int = colorNormal  //标题正常颜色
    private var mTitleColorSelected: Int = colorSelector  //标题选中颜色
    private var mProgress = -1f
    private var mDirection = true
    private var mTextWidth = 0f
    private var mText: String = text

    override fun onDraw(canvas: Canvas) {
        if (mProgress == -1f) {
            drawText(canvas, mTitleColorNormal, 0f, mTextWidth)
        } else {
            val middle = mProgress * mTextWidth
            if (mDirection) {
                drawText(canvas, mTitleColorNormal, 0f, middle)
                drawText(canvas, mTitleColorSelected, middle, mTextWidth)
            } else {
                drawText(canvas, mTitleColorSelected, 0f, middle)
                drawText(canvas, mTitleColorNormal, middle, mTextWidth)
            }
        }
    }

    private fun drawText(canvas: Canvas, color: Int, start: Float, end: Float) {
        paint.color = color
        // 保存画笔状态
        canvas.save();

        // 截取绘制的内容，待会就只会绘制clipRect设置的参数部分
        canvas.clipRect(start, 0f, end, height.toFloat());

        // 获取文字的范围
        val bounds = Rect();
        paint.getTextBounds(mText, 0, mText.length, bounds);

        // 获取文字的Metrics 用来计算基线
        val fontMetrics = paint.fontMetricsInt;

        // 获取文字的宽高
        val fontTotalHeight = fontMetrics.bottom - fontMetrics.top;

        // 计算基线到中心点的位置
        val offY = fontTotalHeight / 2 - fontMetrics.bottom;

        // 计算基线位置
        val baseline = (measuredHeight + fontTotalHeight) / 2 - offY;

        canvas.drawText(mText, (measuredWidth / 2 - bounds.width() / 2).toFloat(), baseline.toFloat(), paint);
        // 释放画笔状态
        canvas.restore();
    }

    fun callOnChange(progress: Float, direction: Boolean) {
        mProgress = progress
        mDirection = direction
        postInvalidate()
    }

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        paint.textSize = getTextSize()
        mTextWidth = width.toFloat()
    }

}
