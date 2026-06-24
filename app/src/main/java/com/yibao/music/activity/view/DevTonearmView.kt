package com.yibao.music.activity.view


import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.yibao.music.R
import com.yibao.music.base.listener.OnStylusChangeListener
import com.yibao.music.base.listener.StylusState
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class DevTonearmView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var listener: OnStylusChangeListener? = null

    private var ivStylus: ImageView? = null
    private var ivStylusBg: ImageView? = null
    private var ivStylusTop: ImageView? = null

    // 旋转中心点（相对于 TonearmView 容器的坐标）
    private var centerX = 0f
    private var centerY = 0f

    // 旋转限制范围
    private val minDegree = 86f
    private val maxDegree = 120f

    // 关键：联合旋转属性。供手势和 ObjectAnimator 反射调用，确保三张图绝对同步旋转
    var totalRotation: Float
        get() = ivStylus?.rotation ?: 0f
        set(value) {
            ivStylus?.rotation = value
//            ivStylusBg?.rotation = value
//            ivStylusTop?.rotation = value
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ivStylus = findViewById(R.id.iv_stylus)
        ivStylusBg = findViewById(R.id.iv_stylus_bg)
//        ivStylusTop = findViewById(R.id.iv_stylus_top)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            adjustPivot()
        }
    }

    private fun adjustPivot() {
        val stylus = ivStylus ?: return
        val bg = ivStylusBg ?: return
        val top = ivStylusTop ?: return

        // 1. 设置唱针主体本身的旋转轴心 (Pivot)
        val pivotYPx = 48f
        stylus.pivotX = stylus.width / 2f
        stylus.pivotY = pivotYPx

        // 2. 计算出唱针轴心在当前容器中的绝对像素坐标
        centerX = stylus.left + stylus.pivotX
        centerY = stylus.top + stylus.pivotY

        // 3. 数学对齐：动态计算位移差，让圆盖背景的中心点与唱针轴心物理重合
        bg.pivotX = bg.width / 2f
        bg.pivotY = bg.height / 2f
        val currentBgCenterX = bg.left + bg.width / 2f
        val currentBgCenterY = bg.top + bg.height / 2f
        bg.translationX = centerX - currentBgCenterX
        bg.translationY = centerY - currentBgCenterY

        // 4. 数学对齐：让顶层盖帽的中心点与唱针轴心物理重合
        top.pivotX = top.width / 2f
        top.pivotY = top.height / 2f
        val currentTopCenterX = top.left + top.width / 2f
        val currentTopCenterY = top.top + top.height / 2f
        top.translationX = centerX - currentTopCenterX
        top.translationY = centerY - currentTopCenterY
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (!isPointInStylusArea(ev.x, ev.y)) {
                return false
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isPointInStylusArea(x: Float, y: Float): Boolean {
        val stylus = ivStylus ?: return false

        val dx = x - centerX
        val dy = y - centerY
        val distance = sqrt((dx * dx + dy * dy).toDouble())
        val stylusFullLength = stylus.height.toDouble()

        val isDistanceInHead =
            distance > (stylusFullLength * 0.75) && distance < (stylusFullLength * 1.1)

        val radians = atan2(dy.toDouble(), dx.toDouble())
        val touchDegrees = Math.toDegrees(radians).toFloat()
        val currentStylusAbsDegree = totalRotation + 90f

        var angleDiff = abs(touchDegrees - currentStylusAbsDegree)
        if (angleDiff > 180) angleDiff = 360 - angleDiff

        val isAngleValid = angleDiff < 30f

        return isDistanceInHead && isAngleValid
    }

    private var returnAnimator: ObjectAnimator? = null
    private val returnDefaultPositionDegree = 102f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                returnAnimator?.cancel()
                isUserTouching = true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - centerX
                val dy = event.y - centerY
                val radians = atan2(dy.toDouble(), dx.toDouble())
                val degrees = Math.toDegrees(radians).toFloat()
                val constrainedDegrees = degrees.coerceIn(minDegree, maxDegree)

                // 实时联动修改三张图的角度
                totalRotation = constrainedDegrees - 90f

                if (constrainedDegrees in 103.0..120.0) {
                    val progress = (constrainedDegrees - 103) / (120f - 103f)
                    listener?.onStateChanged(StylusState.Adjusting(progress.coerceIn(0f, 1f)))
                } else if (constrainedDegrees < 103f) {
                    listener?.onStateChanged(StylusState.Reset)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isUserTouching = false
                val currentRotation = totalRotation
                val offDegree = returnDefaultPositionDegree - 90

                if (currentRotation < offDegree) {
                    resetStylus()
                    listener?.onStateChanged(StylusState.Reset)
                }
            }
        }
        return true
    }

    var isUserTouching = false
        private set

    private val startDegree = 103f
    private val endDegree = 120f
    private var lastAnimator: ObjectAnimator? = null

    /**
     * 【自动同步】播放音乐时进度更新
     */
    fun updateProgress(progress: Float, animate: Boolean = false) {
        if (isUserTouching) return
        val safeProgress = progress.coerceIn(0f, 1f)
        val targetDegree = startDegree + (endDegree - startDegree) * safeProgress
        val targetRotation = targetDegree - 90f
        lastAnimator?.cancel()
        if (animate) {
            // 动画驱动自定义的 "totalRotation" 属性
            lastAnimator = ObjectAnimator.ofFloat(
                this,
                "totalRotation",
                totalRotation,
                targetRotation
            ).apply {
                duration = 800
                interpolator = DecelerateInterpolator()
                start()
            }
        } else {
            totalRotation = targetRotation
        }
    }

    /**
     * 唱针平滑归位动画
     */
    fun resetStylus() {
        isUserTouching = false
        lastAnimator?.cancel()
        returnAnimator?.cancel()
        // 动画驱动自定义的 "totalRotation" 属性
        returnAnimator = ObjectAnimator.ofFloat(this, "totalRotation", totalRotation, 0f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }
}