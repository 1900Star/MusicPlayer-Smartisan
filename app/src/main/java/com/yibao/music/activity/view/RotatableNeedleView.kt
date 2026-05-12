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
import com.yibao.music.util.LogUtil
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class RotatableNeedleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var listener: OnStylusChangeListener? = null

    private var ivStylus: ImageView? = null

    // 旋转中心点（相对于 TonearmView 容器的坐标）
    private var centerX = 0f
    private var centerY = 0f

    // 旋转限制范围
    private val minDegree = 86f
    private val maxDegree = 120f

    override fun onFinishInflate() {
        super.onFinishInflate()
        ivStylus = findViewById(R.id.iv_stylus)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            adjustPivot()
        }
    }

    private fun adjustPivot() {
        val stylus = ivStylus ?: return

        // 1. 设置图片自身的旋转轴心 (Pivot)
        // 假设圆盖中心高度为 48px，请根据实际素材微调
        val pivotYPx = 48f
        stylus.pivotX = stylus.width / 2f
        stylus.pivotY = pivotYPx

        // 2. 计算触摸计算的圆心坐标 (相对于容器)
        // 考虑到 layout_gravity="end"，left 会随布局变化
        centerX = stylus.left + stylus.pivotX
        centerY = stylus.top + stylus.pivotY
    }

    /**
     * 关键点：分发事件拦截
     * 如果点击点不在唱针感应区，则不消耗事件，让底层 View (唱盘) 响应
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (!isPointInStylusArea(ev.x, ev.y)) {
                // 不在感应区，返回 false 使得后续事件不再流向此 View
                return false
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun isPointInStylusArea(x: Float, y: Float): Boolean {
        val stylus = ivStylus ?: return false

        // 1. 计算触摸点距离旋转中心的直线距离
        val dx = x - centerX
        val dy = y - centerY
        val distance = sqrt((dx * dx + dy * dy).toDouble())

        // 2. 获取唱针图片的实际高度（总长度）
        val stylusFullLength = stylus.height.toDouble()

        // 3. 判定距离：只有点击在唱针末端区域才响应
        // 假设磁头部分大约占唱针末端的 25% 长度（即距离中心 75% 到 110% 处）
        // 1.1 是为了给边缘留出一点手感容错
        val isDistanceInHead =
            distance > (stylusFullLength * 0.75) && distance < (stylusFullLength * 1.1)

        // 4. 判定角度：手指角度必须在唱针当前角度附近（保持之前的逻辑）
        val radians = atan2(dy.toDouble(), dx.toDouble())
        val touchDegrees = Math.toDegrees(radians).toFloat()
        val currentStylusAbsDegree = stylus.rotation + 90f // 垂直向下是 90 度

//        LogUtil.d("===", "-----点击角度：$touchDegrees   ----  $currentStylusAbsDegree")
        // 计算角度差（处理 -180/180 度跳变）
        var angleDiff = abs(touchDegrees - currentStylusAbsDegree)
        if (angleDiff > 180) angleDiff = 360 - angleDiff

        val isAngleValid = angleDiff < 30f // 30度容错，方便抓取

        // 只有距离和角度同时满足（即点击点落在磁头那一块区域），才接管事件
        return isDistanceInHead && isAngleValid
    }

    // 在类中定义一个动画变量，方便在下一次触摸时取消它
    private var returnAnimator: ObjectAnimator? = null
    private val returnDefaultPositionDegree = 102f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 如果正在回弹动画中，用户再次触摸，立即停止动画
                returnAnimator?.cancel()
                isUserTouching = true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - centerX
                val dy = event.y - centerY
                val radians = atan2(dy.toDouble(), dx.toDouble())
                val degrees = Math.toDegrees(radians).toFloat()
                // 实时跟随手指旋转，并限制在指定角度范围内
                val constrainedDegrees = degrees.coerceIn(minDegree, maxDegree)
                LogUtil.d("===", "-----实时角度：$degrees   ----  $constrainedDegrees")
                ivStylus?.rotation = constrainedDegrees - 90f


                // 业务逻辑判断
                if (constrainedDegrees in 103.0..120.0) {
                    // 计算进度：120度是起始(0.0)，103度是结束(1.0)
                    // 公式：(120 - 当前) / (120 - 103)
                    val progress = (120f - constrainedDegrees) / (120f - 103f)
                    listener?.onStateChanged(StylusState.Adjusting(progress.coerceIn(0f, 1f)))
                } else if (constrainedDegrees < 103f) {
                    // 小于103度，发送暂停信号
                    listener?.onStateChanged(StylusState.Reset)
                }


            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isUserTouching = false
                // 关键逻辑：松手时判断角度,小于某个角度唱针自动归位
                val currentRotation = ivStylus?.rotation ?: 0f
                // 注意：因为 ivStylus.rotation = degrees - 90
                // 所以 103 度对应 rotation 为 13 度 (103 - 90)

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

    private val DEGREE_START = 120f
    private val DEGREE_END = 103f
    private val RESET_ROTATION = 0f

    /**
     * 【自动同步】播放音乐时，Activity 每秒多次调用此方法
     */
    private var lastAnimator: ObjectAnimator? = null
    fun updateProgress(progress: Float, animate: Boolean = false) {
        if (isUserTouching) return
        val safeProgress = progress.coerceIn(0f, 1f)
        val targetDegree = DEGREE_START - (DEGREE_START - DEGREE_END) * safeProgress
        val targetRotation = targetDegree - 90f
        lastAnimator?.cancel()
        if (animate) {
            lastAnimator = ObjectAnimator.ofFloat(
                ivStylus,
                "rotation",
                ivStylus?.rotation ?: 0f,
                targetRotation
            ).apply {
                duration = 400
                interpolator = DecelerateInterpolator()
                start()
            }
        } else {
            ivStylus?.rotation = targetRotation
        }
    }

    /**
     * 唱针平滑归位动画
     */
    fun resetStylus() {
        val stylus = ivStylus ?: return
        isUserTouching = false
        // 从当前旋转角度平滑过渡到 0 (垂直向下)
        returnAnimator = ObjectAnimator.ofFloat(stylus, "rotation", stylus.rotation, 0f).apply {
            duration = 300 // 归位时长 300 毫秒
            // 使用减速插值器，让归位动作先快后慢，更真实
            interpolator = DecelerateInterpolator()
            start()
        }
    }

}
