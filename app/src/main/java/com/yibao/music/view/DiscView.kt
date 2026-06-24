package com.yibao.music.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.core.animation.doOnEnd
import com.yibao.music.base.listener.OnDiscTouchListener
import kotlin.math.atan2

class DiscView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private var currentRotation = 0f
    private var isUserTouching = false


    var discListener: OnDiscTouchListener? = null

    // 自动旋转动画
    var autoAnimator: ValueAnimator? = null

    // 惯性动画
    var flingAnimator: ValueAnimator? = null

    // 手势检测，用于计算 Fling
    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                handleScroll(e2.x, e2.y)
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                startFling(velocityX, velocityY)
                return true
            }
        })

    private fun handleScroll(x: Float, y: Float) {
        val centerX = width / 2f
        val centerY = height / 2f
        // 计算当前手指相对于中心点的角度
        val angle =
            Math.toDegrees(atan2((y - centerY).toDouble(), (x - centerX).toDouble())).toFloat()

        if (lastAngle != 0f) {
            var diff = angle - lastAngle
            // 处理 180/-180 跳变
            if (diff > 180) diff -= 360
            if (diff < -180) diff += 360

            updateRotation(currentRotation + diff)
            // 这里可以回调给 Activity 做搓碟音效：diff 代表了瞬时速度
            discListener?.onActionMove(currentRotation, diff)
        }
        lastAngle = angle
    }

    private var lastAngle = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 1. 让手势检测器先行处理（处理 Fling 等）
        val gestureConsumed = gestureDetector.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isUserTouching = true
                autoAnimator?.pause()
                flingAnimator?.cancel()

                val centerX = width / 2f
                val centerY = height / 2f
                lastAngle = Math.toDegrees(
                    atan2((event.y - centerY).toDouble(), (event.x - centerX).toDouble())
                ).toFloat()
            }

            MotionEvent.ACTION_MOVE -> {
                // 手动调用滑动处理，确保即使手势检测器没反应，唱片也能跟手
                handleScroll(event.x, event.y)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isUserTouching = false
                // 如果手势检测器没有触发 Fling 动画，我们才恢复自动旋转
                if (flingAnimator == null || !flingAnimator!!.isRunning) {
                    autoAnimator?.resume()
                }
                lastAngle = 0f
            }
        }
        // 2. 关键：必须返回 true，否则 ACTION_DOWN 之后再也收不到 MOVE 和 UP
        return true
    }


    private fun updateRotation(rot: Float) {
        currentRotation = rot % 360
        rotation = currentRotation
    }

    // 惯性逻辑
    private fun startFling(vx: Float, vy: Float) {
        // 将线性速度转为角速度 (估算值)
        val angularVelocity = (vx + vy) / 50f
        flingAnimator = ValueAnimator.ofFloat(angularVelocity, 0f).apply {
            duration = 1500 // 惯性持续时间
            interpolator = android.view.animation.DecelerateInterpolator()
            addUpdateListener {
                if (!isUserTouching) {
                    val v = it.animatedValue as Float
                    updateRotation(currentRotation + v)
                }
            }
            // 惯性结束后恢复自动旋转
            doOnEnd { if (!isUserTouching) autoAnimator?.resume() }
            start()
        }
    }

    fun initAutoRotation() {
        autoAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 15000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                if (!isUserTouching && (flingAnimator == null || !flingAnimator!!.isRunning)) {
                    // 自动旋转时累加角度
                    updateRotation(currentRotation + 360f / (15000 / 16f)) // 每帧约 60fps 增量
                }
            }
            start()
        }
    }


}

