package com.yibao.music.view.music


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.graphics.withSave
import androidx.core.graphics.withTranslation
import com.yibao.music.R
import com.yibao.music.model.MusicLyricBean
import com.yibao.music.util.ColorUtil
import com.yibao.music.util.Constant
import kotlin.math.abs

/**
 * Des：自定义歌词View（支持长歌词自动换行与手动拖拽回弹）
 * Time: 2026/06/25
 * @author Stran & Optimize
 */
class LyricsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    // 核心绘制画笔
    private val selectedPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private val normalPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)

    private var mViewW = 0
    private var mViewH = 0

    private var musicLyrList: List<MusicLyricBean> = ArrayList()
    private val lyricNodes = ArrayList<LyricNode>()

    private var centerLine = 0
    private var currentProgress = 0
    private var duration = 0
    private var mLyricsMsg: String = ""
    private var singleLineLayout: StaticLayout? = null

    // 样式与间距配置
    private var mBigText = 0f
    private var smallText = 0f
    private var mLyricsSelected = 0
    private var mLyricsNormal = 0
    private var lineSpaceExtra = 0 // 每块歌词之间的额外间距

    // 手势控制变量
    private var lastTouchY = 0f
    private var isUserScrolling = false // 是否处于手动拖拽或回弹状态
    private var touchSlop = 0
    private var manualScrollOffset = 0f // 手动滑动的累计偏移量
    private var autoTargetCenter = 0f   // 自动播放时完美的视窗中心点坐标
    private var snapBackAnimator: ValueAnimator? = null

    init {
        initView()
    }

    private fun initView() {
        mLyricsSelected = ColorUtil.lyricsSelecte
        mLyricsNormal = ColorUtil.lyricsNormal
        mBigText = resources.getDimension(R.dimen.bigLyrics)
        smallText = resources.getDimension(R.dimen.smallLyrics)
        lineSpaceExtra = resources.getDimensionPixelSize(R.dimen.line_height) / 2 // 取原行高的一半作为空隙

        selectedPaint.color = mLyricsSelected
        selectedPaint.textSize = mBigText

        normalPaint.color = mLyricsNormal
        normalPaint.textSize = smallText

        ellipsize = TextUtils.TruncateAt.MARQUEE
        maxLines = 2
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewW = w
        mViewH = h
        rebuildLyricNodes()
    }

    /**
     * 核心数据结构：承载每一行歌词节点的信息
     */
    private class LyricNode(
        val bean: MusicLyricBean,
        val selectedLayout: StaticLayout,
        val normalLayout: StaticLayout,
        val allocatedHeight: Int, // 锁定的分配高度，防止大小字切换时抖动
        var topY: Float = 0f       // 在歌词长卷中的绝对Y轴起始坐标
    )

    /**
     * 当数据源或View大小改变时，重新进行高精度的文本排版测量
     */
    private fun rebuildLyricNodes() {
        lyricNodes.clear()
        val layoutWidth = mViewW - paddingLeft - paddingRight
        if (layoutWidth <= 0 || musicLyrList.isEmpty()) {
            updateSingleLineLayout()
            return
        }

        var currentTop = 0f
        for (bean in musicLyrList) {
            val sLayout = createStaticLayout(bean.content, selectedPaint, layoutWidth)
            val nLayout = createStaticLayout(bean.content, normalPaint, layoutWidth)
            // 取两者的最大高度作为该行歌词的恒定占位高度，规避行数变化导致的画跳动
            val maxLayoutHeight = sLayout.height.coerceAtLeast(nLayout.height)

            val node = LyricNode(bean, sLayout, nLayout, maxLayoutHeight, currentTop)
            lyricNodes.add(node)

            currentTop += maxLayoutHeight + lineSpaceExtra
        }
    }

    private fun updateSingleLineLayout() {
        val layoutWidth = 100.coerceAtLeast(mViewW - paddingLeft - paddingRight)
        val text =
            if (mLyricsMsg == Constant.PURE_MUSIC) Constant.PURE_MUSIC else Constant.NO_LYRICS
        singleLineLayout = createStaticLayout(text, normalPaint, layoutWidth)
    }

    private fun createStaticLayout(text: CharSequence, paint: TextPaint, width: Int): StaticLayout {

        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
    }

    override fun onDraw(canvas: Canvas) {
        // 调用标准 super 可能会引发原生 TextView 绘制，在此不进行原声文字绘制
        if (lyricNodes.isEmpty()) {
            drawSingLine(canvas)
            return
        }

        // 计算当前视觉中心点应该对齐的虚拟大画布的 Y 坐标
        val currentTargetCenter = autoTargetCenter + manualScrollOffset

        // 遍历绘制可见区域内的歌词节点
        for (i in lyricNodes.indices) {
            val node = lyricNodes[i]
            // 计算当前节点应该绘制在 Canvas 上的绝对 Y 轴起始坐标
            val canvasY = (mViewH / 2f) + (node.topY - currentTargetCenter)

            // 越界剪裁优化：如果完全超出了屏幕可见上下边界，则跳过绘制
            if (canvasY + node.allocatedHeight < 0 || canvasY > mViewH) {
                continue
            }

            canvas.withSave {
                // 选取对应的样式布局（高亮或普通）
                val layout = if (i == centerLine) node.selectedLayout else node.normalLayout
                // 使得短歌词在分配的预留高度内居中
                val textOffset = (node.allocatedHeight - layout.height) / 2f

                translate(paddingLeft.toFloat(), canvasY + textOffset)
                layout.draw(this)
            }
        }
    }

    private fun drawSingLine(canvas: Canvas) {
        val layout = singleLineLayout ?: return
        val x = paddingLeft.toFloat()
        val y = (mViewH - layout.height) / 2f
        canvas.withTranslation(x, y) {
            layout.draw(this)
        }
    }

    /**
     * 根据歌曲播放进度滚动歌词
     */
    fun rollText(progress: Int, duration: Int) {
        if (musicLyrList.isEmpty()) return

        this.currentProgress = progress
        this.duration = duration

        // 1. 计算自然播放下处于视觉中心的行数
        val startTime = musicLyrList.last().startTime
        val autoCenterLine = if (progress >= startTime) {
            musicLyrList.size - 1
        } else {
            var index = 0
            for (i in 0 until musicLyrList.size - 1) {
                if (progress >= musicLyrList[i].startTime && progress < musicLyrList[i + 1].startTime) {
                    index = i
                    break
                }
            }
            index
        }

        // 2. 计算当前行平滑滚动的百分比偏置
        val lineTime = if (autoCenterLine == musicLyrList.size - 1) {
            duration - musicLyrList[autoCenterLine].startTime
        } else {
            musicLyrList[autoCenterLine + 1].startTime - musicLyrList[autoCenterLine].startTime
        }
        val offsetTime = progress - musicLyrList[autoCenterLine].startTime
        val offsetTimePercent = if (lineTime > 0) offsetTime / lineTime.toFloat() else 0f

        // 3. 动态更新自动播放状态下的标准目标中心线
        if (lyricNodes.isNotEmpty() && autoCenterLine < lyricNodes.size) {
            val currentCenter =
                lyricNodes[autoCenterLine].topY + lyricNodes[autoCenterLine].allocatedHeight / 2f
            autoTargetCenter = if (autoCenterLine < lyricNodes.size - 1) {
                val nextCenter =
                    lyricNodes[autoCenterLine + 1].topY + lyricNodes[autoCenterLine + 1].allocatedHeight / 2f
                currentCenter + (nextCenter - currentCenter) * offsetTimePercent
            } else {
                currentCenter
            }
        }

        // 4. 如果用户当前没有执掌手势拖拽，直接应用自然更新
        if (!isUserScrolling) {
            centerLine = autoCenterLine
            manualScrollOffset = 0f
            invalidate()
        }
    }

    /**
     * 手势触摸拦截事件，处理手动上下拖拽
     */
    // 用于辅助判断点击
    private var startX = 0f
    private var startY = 0f
    private var isDragging = false // 标记当前手势是否已经触发了滚动

    // 2. 修改后的 onTouchEvent 方法
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 如果没有歌词（显示纯音乐或无歌词提示），也允许点击关闭
        if (lyricNodes.isEmpty()) {
            if (event.action == MotionEvent.ACTION_UP) {
                performClick()
            }
            return true
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录手指按下的起始坐标
                startX = event.x
                startY = event.y
                lastTouchY = event.y
                isDragging = false // 初始化拖拽状态

                // 按下时先暂停可能正在运行的回弹动画
                snapBackAnimator?.cancel()
                removeCallbacks(snapBackRunnable)
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX = event.x
                val currentY = event.y

                // 🎯 【核心判定】如果尚未触发拖拽，计算手指移动距离是否超过了系统的静止阈值 (touchSlop)
                if (!isDragging) {
                    val dx = abs(currentX - startX)
                    val dy = abs(currentY - startY)
                    if (dy > touchSlop || dx > touchSlop) {
                        isDragging = true
                        isUserScrolling = true // 真正确认为用户正在手动滚动歌词
                    }
                }

                // 只有确认是拖拽状态，才更新歌词滚动的偏移量
                if (isDragging) {
                    val deltaY = currentY - lastTouchY
                    manualScrollOffset -= deltaY
                    lastTouchY = currentY

                    // 实时计算当前居中的歌词行
                    val currentTargetCenter = autoTargetCenter + manualScrollOffset
                    centerLine = lyricNodes.minByOrNull {
                        abs((it.topY + it.allocatedHeight / 2f) - currentTargetCenter)
                    }?.let { lyricNodes.indexOf(it) } ?: 0

                    invalidate()
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (event.action == MotionEvent.ACTION_UP && !isDragging) {
                    // 💡 【触发点击】手指抬起且从未移动过大距离 -> 判定为标准单次点击
                    isUserScrolling = false
                    performClick() // 调用 View 的标准点击回调
                } else {
                    // 如果是滚动后松手，触发正常的 2.5 秒回弹逻辑
                    if (isDragging) {
                        postDelayed(snapBackRunnable, 2500)
                    } else {
                        isUserScrolling = false
                    }
                }
            }
        }
        return true
    }

    // 3. ✨ 必须重写此方法！以保证标准 setOnClickListener 能正常收到回调，同时满足 Android 无障碍辅助功能
    override fun performClick(): Boolean {
        super.performClick() // 这行代码会触发外部设置的 OnClickListener
        return true
    }


    /**
     * 平稳平滑的回弹逻辑
     */
    private val snapBackRunnable = Runnable {
        snapBackAnimator?.cancel()
        // 将积累的手动滑动偏差值 manualScrollOffset 平滑消减至 0
        snapBackAnimator = ValueAnimator.ofFloat(manualScrollOffset, 0f).apply {
            duration = 500
            addUpdateListener { animation ->
                manualScrollOffset = animation.animatedValue as Float

                // 回弹过程中同样保持中线焦点动态切换
                val currentTargetCenter = autoTargetCenter + manualScrollOffset
                centerLine = lyricNodes.minByOrNull {
                    abs((it.topY + it.allocatedHeight / 2f) - currentTargetCenter)
                }?.let { lyricNodes.indexOf(it) } ?: 0

                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isUserScrolling = false
                }
            })
            start()
        }
    }

    fun setLrcFile(lrcList: List<MusicLyricBean>?, lyricsMsg: String?) {
        mLyricsMsg = lyricsMsg ?: ""
        musicLyrList = lrcList ?: emptyList()
        centerLine = 0
        autoTargetCenter = 0f
        manualScrollOffset = 0f
        isUserScrolling = false
        removeCallbacks(snapBackRunnable)
        snapBackAnimator?.cancel()
        rebuildLyricNodes()
        invalidate()
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(snapBackRunnable)
        snapBackAnimator?.cancel()
        super.onDetachedFromWindow()
    }
}