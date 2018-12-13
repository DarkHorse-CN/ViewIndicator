package com.darkhorse.viewindicator

import android.content.Context
import android.graphics.*
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

/**
 * Description:
 * Created by DarkHorse on 2018/6/28.
 */
class ViewIndicator : LinearLayout {

    companion object {
        val COLOR_TITLE_NORMAL = Color.parseColor("#000000")     //标题正常颜色
        val COLOR_TITLE_SELECTED = Color.parseColor("#DC143C")  //标题选中颜色
    }

    private var mVisibleCount: Int = 4 //可见Tab数量
    private var mTitleSize: Int = 14           //标题字体大小
    private var mTitleColorNormal: Int = COLOR_TITLE_NORMAL
    private var mTitleColorSelected: Int = COLOR_TITLE_SELECTED
    private var mPointerType: Int = 0  //指示器样式
    private val LINE = 1
    private val TRIGANGLE = 2
    private val BACKGROUND = 3

    //画笔
    private val mPaint: Paint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true        //是否光滑，抗锯齿
        paint.color = mPointerColor
        paint.style = Paint.Style.FILL
        paint.pathEffect = CornerPathEffect(3f)   //设置画笔的路径效果
        paint
    }

    //指示器图形
    private val mPath: Path by lazy {
        Path()
    }

    private var mPointerWidth: Int   //指示器宽度
    private var mPointerHeight: Int //指示器高度
    private var mTabWidth: Int   //tab宽度

    private var mInitTranslationX: Int = 0      //指示器初始偏移量
    private var mTranslationX: Int = 0      //指示器偏移量
    private var mScrollX: Int = 0      //标题偏移量
    private var mPointerColor: Int = mTitleColorSelected     //指示器颜色

    private lateinit var mTitles: Array<String>    //标题组
    private var mTabItemClickListener: TabItemClickListener? = null //标题点击事件
    private var mViewPager: ViewPager? = null //绑定ViewPager

    var isLock = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ViewIndicator)

        //获取可见Tab的数量
        mVisibleCount = typeArray.getInt(R.styleable.ViewIndicator_visible_count, 4)
        mTabWidth = mScreenWidth / mVisibleCount

        //获取Title的字体大小
        mTitleSize = typeArray.getInt(R.styleable.ViewIndicator_title_size, 14)

        //获取可见Tab的默认颜色和被选中时的颜色
        mTitleColorNormal = typeArray.getInt(R.styleable.ViewIndicator_title_color_normal, COLOR_TITLE_NORMAL)
        mTitleColorSelected = typeArray.getInt(R.styleable.ViewIndicator_title_color_selected, COLOR_TITLE_SELECTED)

        //获取指示器类型
        mPointerType = typeArray.getInt(R.styleable.ViewIndicator_pointer_type, 1)

        //获取指示器颜色
        mPointerColor = typeArray.getInt(R.styleable.ViewIndicator_pointer_color, mTitleColorSelected)

        //获取指示器宽度度
        mPointerWidth = (typeArray.getFloat(R.styleable.ViewIndicator_pointer_percent, 1f) * mTabWidth).toInt()

        //获取指示器高度
        val height = typeArray.getInt(R.styleable.ViewIndicator_pointer_height, 2)
        mPointerHeight = (height * context.resources.displayMetrics.density + 0.5f).toInt()

        typeArray.recycle()

    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (mPointerType != 0) {
            canvas?.save()
            canvas?.translate((mInitTranslationX + mTranslationX).toFloat(), height.toFloat())
            canvas?.drawPath(mPath, mPaint)
            canvas?.restore()
        }
        super.dispatchDraw(canvas)
    }

    /**
     * 控件宽高发生变化的时候调用该方法
     *
     * @param w    控件宽度
     * @param h    控件高度
     * @param oldw
     * @param oldh
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        when (mPointerType) {
            TRIGANGLE -> {
                mPointerWidth = mTabWidth / 6
                mPointerHeight = mPointerWidth / 2
            }
            BACKGROUND -> {
                mPointerWidth = mTabWidth
                mPointerHeight = h
            }
        }

        mInitTranslationX = (mTabWidth - mPointerWidth) / 2
        initPointer()
    }

    /**
     * 绘制指示器
     */
    private fun initPointer() {
        when (mPointerType) {
            LINE -> {
                mPath.moveTo(0f, 0f)
                mPath.lineTo(mPointerWidth.toFloat(), 0f)
                mPath.lineTo(mPointerWidth.toFloat(), (-mPointerHeight).toFloat())
                mPath.lineTo(0f, (-mPointerHeight).toFloat())
                mPath.close()
            }
            TRIGANGLE -> {
                mPath.moveTo(0f, 0f)
                mPath.lineTo(mPointerWidth.toFloat(), 0f)
                mPath.lineTo((mPointerWidth / 2).toFloat(), (-mPointerHeight).toFloat())
                mPath.close()
            }
            BACKGROUND -> {
                mPath.moveTo(0f, 0f)
                mPath.lineTo(mPointerWidth.toFloat(), 0f)
                mPath.lineTo(mPointerWidth.toFloat(), (-mPointerHeight).toFloat())
                mPath.lineTo(0f, (-mPointerHeight).toFloat())
                mPath.close()
            }
        }
    }

    /**
     * 指示器跟随Tab位置进行移动
     *
     * @param position
     * @param positionOffset
     */
    private fun pointerScroll(position: Int, positionOffset: Float) {
        mTranslationX = (mTabWidth * (positionOffset + position)).toInt()
        var view: View = this
        if (parent is HorizontalScrollView) {
            view = parent as View
        }

        val distant = mTranslationX - mScrollX

        if (distant > (mVisibleCount - 1) * mTabWidth) {
            mScrollX += distant - (mVisibleCount - 1) * mTabWidth
        } else if (distant < 0) {
            mScrollX += distant
        }
        view.scrollTo(mScrollX, 0)
        postInvalidate()
    }

    /**
     * 选定的Tab
     * @param position
     */
    private fun setTab(position: Int) {
        for (i in 0 until childCount) {
            val textView = getChildAt(i) as ColorTrackTextView
            textView.callOnChange(1f, true)
        }
        invalidate(position, 0f)
    }

    fun invalidate(position: Int, positionOffset: Float = 0f) {
        val curTextView = getChildAt(position) as ColorTrackTextView
        curTextView.callOnChange(positionOffset, true);

        if (position < childCount - 1) {
            val nextTextView = getChildAt(position + 1) as ColorTrackTextView
            nextTextView.callOnChange(positionOffset, false)
        }
        pointerScroll(position, positionOffset)
    }

    /**
     * 根据title创建tab数量
     *
     * @param title
     * @return
     */
    private fun generateTextView(position: Int, title: String): ColorTrackTextView {
        val tv = ColorTrackTextView(context, mTitleColorNormal, mTitleColorSelected, mTitleSize, mTabWidth, title)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        lp.width = mTabWidth
        tv.gravity = Gravity.CENTER
        tv.layoutParams = lp

        tv.setOnClickListener {
            if (!isLock) {
                setTab(position)
                mTabItemClickListener?.onTabItemClickListener(position)
                mViewPager?.setCurrentItem(position, false)
            }
        }
        return tv
    }

    /**
     * 初始化控件,实现TabItem点击事件
     */
    fun init(titles: Array<String>?, viewClickListener: TabItemClickListener? = null) {
        mViewPager = null
        if (viewClickListener != null) {
            mTabItemClickListener = viewClickListener
        }

        if (titles != null) {
            mTitles = titles
        }
        this.removeAllViews()
        if (mTitles.isNotEmpty()) {
            for (i in mTitles.indices) {
                this.addView(generateTextView(i, mTitles[i]))
            }
            invalidate(0, 0f)
        }
    }

    fun init(titles: Array<String>?, viewPager: ViewPager) {
        mViewPager = viewPager
        mTabItemClickListener = null

        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
                if (p0 == ViewPager.SCROLL_STATE_DRAGGING) {
                    isLock = true
                } else if (p0 == ViewPager.SCROLL_STATE_IDLE) {
                    isLock = false
                }
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                invalidate(p0, p1)
            }

            override fun onPageSelected(p0: Int) {
            }
        })

        if (titles != null) {
            mTitles = titles
        }
        this.removeAllViews()
        if (mTitles.isNotEmpty()) {
            for (i in mTitles.indices) {
                this.addView(generateTextView(i, mTitles[i]))
            }
            invalidate(0, 0f)
        }
    }


    /**
     * 获取屏幕宽度
     *
     * @return
     */
    private val mScreenWidth: Int
        get() {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            return outMetrics.widthPixels
        }
}
