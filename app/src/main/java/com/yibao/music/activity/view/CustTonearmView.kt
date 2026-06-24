package com.yibao.music.activity.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import com.yibao.music.R
import com.yibao.music.base.listener.OnStylusChangeListener
import com.yibao.music.base.listener.StylusState
import com.yibao.music.databinding.CustTonearmViewBinding
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class CustTonearmView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var returnAnimator: ObjectAnimator? = null
    private val returnDefaultPositionDegree = 102f
    var isUserTouching = false
        private set

    private val startDegree = 103f
    private val endDegree = 120f
    private var lastAnimator: ObjectAnimator? = null

    // 旋转中心点（相对于当前组合控件的绝对坐标，用于手势判定）
    private var centerX = 0f
    private var centerY = 0f

    private val minDegree = 86f
    private val maxDegree = 120f

    private val shadowAngleOffset = 2f
    private var ivStylusShadow: ImageView? = null

    var listener: OnStylusChangeListener? = null
    private val mBinding = CustTonearmViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var pivotsInitialized = false

    init {
        clipChildren = false
        clipToPadding = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ivStylusShadow = (parent as? ViewGroup)?.findViewById(R.id.iv_stylus_shadow)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (width > 0) {
            post { initPivots() }
        }
    }

    /**
     * 读取视觉锚点，高精度绑定轴心
     */
    private fun initPivots() {
        val container = mBinding.rotationContainer
        val anchor = mBinding.viewPivotAnchor
        val shadow = ivStylusShadow

        if (anchor.width == 0) return

        // 1. 直接读取你在 XML 里纯手工视觉对齐好的红点作为唯一旋转轴心
        val pivotX = anchor.left + anchor.width / 2f
        val pivotY = anchor.top + anchor.height / 2f

        // 2. 注入旋转容器
        container.pivotX = pivotX
        container.pivotY = pivotY

        // 3. 换算得到绝对手势判定中心
        centerX = container.left + pivotX
        centerY = container.top + pivotY

        // 4. 同步配置外部阴影的旋转中心
        shadow?.let {
            val parentCenterX = this.left + centerX
            val parentCenterY = this.top + centerY
            it.pivotX = parentCenterX - it.left
            it.pivotY = parentCenterY - it.top
        }

        pivotsInitialized = true
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!pivotsInitialized) initPivots()
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (!isPointInStylusArea(ev.x, ev.y)) return false
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isPointInStylusArea(x: Float, y: Float): Boolean {
        if (!pivotsInitialized) return false
        val dx = x - centerX
        val dy = y - centerY
        val distance = sqrt((dx * dx + dy * dy).toDouble())

        val stylusFullLength = mBinding.ivStylus.height.toDouble()
        val isDistanceInHead = distance > 0 && distance < (stylusFullLength * 1.2)

        val radians = atan2(dy.toDouble(), dx.toDouble())
        var touchDegrees = Math.toDegrees(radians).toFloat()
        if (touchDegrees < 0) touchDegrees += 360f

        val currentStylusAbsDegree = mBinding.rotationContainer.rotation + 90f

        var angleDiff = abs(touchDegrees - currentStylusAbsDegree)
        if (angleDiff > 180) angleDiff = 360 - angleDiff

        return isDistanceInHead && angleDiff < 45f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!pivotsInitialized) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                returnAnimator?.cancel()
                lastAnimator?.cancel()
                isUserTouching = true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - centerX
                val dy = event.y - centerY
                val radians = atan2(dy.toDouble(), dx.toDouble())
                var degrees = Math.toDegrees(radians).toFloat()
                if (degrees < 0) degrees += 360f

                val constrainedDegrees = degrees.coerceIn(minDegree, maxDegree)
                val targetRotation = constrainedDegrees - 90f

                mBinding.rotationContainer.rotation = targetRotation
                ivStylusShadow?.rotation = targetRotation - shadowAngleOffset

                if (constrainedDegrees in startDegree..endDegree) {
                    val progress = (constrainedDegrees - startDegree) / (endDegree - startDegree)
                    listener?.onStateChanged(StylusState.Adjusting(progress.coerceIn(0f, 1f)))
                } else if (constrainedDegrees < startDegree) {
                    listener?.onStateChanged(StylusState.Reset)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isUserTouching = false
                val currentRotation = mBinding.rotationContainer.rotation
                val offDegree = returnDefaultPositionDegree - 90

                if (currentRotation < offDegree) {
                    resetStylus()
                    listener?.onStateChanged(StylusState.Reset)
                }
            }
        }
        return true
    }

    fun updateProgress(progress: Float, animate: Boolean = false) {
        if (isUserTouching) return
        val safeProgress = progress.coerceIn(0f, 1f)
        val targetDegree = startDegree + (endDegree - startDegree) * safeProgress
        val targetRotation = targetDegree - 90f

        lastAnimator?.cancel()
        if (animate) {
            lastAnimator = ObjectAnimator.ofFloat(
                mBinding.rotationContainer, "rotation",
                mBinding.rotationContainer.rotation, targetRotation
            ).apply {
                duration = 800
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    val currentRot = animator.animatedValue as Float
                    ivStylusShadow?.rotation = currentRot - shadowAngleOffset
                }
                start()
            }
        } else {
            mBinding.rotationContainer.rotation = targetRotation
            ivStylusShadow?.rotation = targetRotation - shadowAngleOffset
        }
    }

    fun resetStylus() {
        isUserTouching = false
        lastAnimator?.cancel()
        returnAnimator?.cancel()

        returnAnimator = ObjectAnimator.ofFloat(
            mBinding.rotationContainer, "rotation",
            mBinding.rotationContainer.rotation, 0f
        ).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                val currentRot = animator.animatedValue as Float
                ivStylusShadow?.rotation = currentRot - shadowAngleOffset
            }
            start()
        }
    }
}