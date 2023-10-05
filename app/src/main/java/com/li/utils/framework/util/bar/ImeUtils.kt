package com.li.utils.framework.util.bar

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

/**
 * 输入法工具了
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/16
 */
object ImeUtils {

    /**
     * 用于监听输入法高度的接口
     * */
    internal interface ImeHeightListener {

        /**
         * 开始变化前的准备
         * */
        fun onPrepare(action: () -> Unit)

        /**
         * 高度变化
         * @param action
         *  - height: 当前高度
         *  - fraction: 百分比
         *  - offset: 变化值
         * */
        fun onChange(action: (height: Int, fraction: Float, offset: Int) -> Unit)

        /**
         * Ime准备开始变化
         * */
        fun onStart(action: () -> Unit)

        /**
         * Ime结束变化
         * */
        fun onEnd(action: () -> Unit)
    }

    /**
     * 获取监听高度的 Builder
     * */
    fun heightListenerBuilder(): ImeHeightListenerBuilder {
        return ImeHeightListenerBuilder()
    }

    /**
     * 用于监听输入法高度变化
     * */
    class ImeHeightListenerBuilder {

        private var mOnChange: ((Int, Float, Int) -> Unit)? = null

        private var mOnPrepare: (() -> Unit)? = null

        private var mOnStart: (() -> Unit)? = null

        private var mOnEnd: (() -> Unit)? = null

        private var mView: View? = null

        fun onChange(action: (height: Int, fraction: Float, offset: Int) -> Unit): ImeHeightListenerBuilder {
            this.mOnChange = action
            return this
        }

        fun onStart(action: () -> Unit): ImeHeightListenerBuilder {
            this.mOnStart = action
            return this
        }

        fun onEnd(action: () -> Unit): ImeHeightListenerBuilder {
            this.mOnEnd = action
            return this
        }

        fun onPrepare(action: () -> Unit): ImeHeightListenerBuilder {
            this.mOnPrepare = action
            return this
        }

        /**
         * 构架 Listener
         * @param window
         * */
        fun build(window: Window): ImeHeightListenerBuilder {
            mView = window.decorView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setImeHeightListenerUpperQ(
                    window.decorView,
                    onPrepare = mOnPrepare ?: {},
                    onStart = mOnStart ?: {},
                    onEnd = mOnEnd ?: {},
                    onProgress = mOnChange ?: { _, _, _ -> }
                )
            } else {
                setImeHeightListenerBelowQ(
                    window.decorView,
                    onPrepare = mOnPrepare ?: {},
                    onStart = mOnStart ?: {},
                    onEnd = mOnEnd ?: {},
                    onProgress = mOnChange ?: { _, _, _ -> }
                )
            }
            return this
        }

        /**
         * 构建 Listener
         * @param activity
         * */
        fun build(activity: Activity): ImeHeightListenerBuilder {
            return build(activity.window)
        }

        /**
         * 取消监听
         * */
        fun cancel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mView?.setWindowInsetsAnimationCallback(null)
            } else {
                ViewCompat.setWindowInsetsAnimationCallback(mView!!, null)
            }
        }


        /**
         * 添加 Ime 高度改变的回调，这是一个连续变化值，包含了高度值以及偏移量，
         * @param view
         * @param onPrepare 动画开始前的一些处理
         * @param onStart 动画准备开始
         * @param onEnd 动画结束
         * @param onProgress height 是当前高度值，offset是偏移量，大于0时为弹出软键盘，小于0为收回软键盘
         * */
        @RequiresApi(Build.VERSION_CODES.R)
        private inline fun setImeHeightListenerUpperQ(
            view: View,
            crossinline onPrepare: () -> Unit,
            crossinline onStart: () -> Unit,
            crossinline onEnd: () -> Unit,
            crossinline onProgress: (height: Int, fraction: Float, offset: Int) -> Unit
        ) {
            var pre = 0
            var anim: WindowInsetsAnimation? = null
            val cb = object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onPrepare(animation: WindowInsetsAnimation) {
                    super.onPrepare(animation)
                    anim = animation
                    animation.durationMillis
                    onPrepare()
                }

                override fun onStart(animation: WindowInsetsAnimation, bounds: WindowInsetsAnimation.Bounds): WindowInsetsAnimation.Bounds {
                    onStart()
                    return super.onStart(animation, bounds)
                }

                override fun onEnd(animation: WindowInsetsAnimation) {
                    super.onEnd(animation)
                    onEnd()
                }

                override fun onProgress(insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>): WindowInsets {
                    val height = insets.getInsets(WindowInsets.Type.ime()).bottom + insets.getInsets(WindowInsets.Type.systemBars()).bottom
                    onProgress(height, anim?.fraction ?: 0f, height - pre)
                    pre = height
                    return insets
                }
            }
            view.setWindowInsetsAnimationCallback(cb)
        }

        /**
         * 伪监听软键盘的高度变化,建议实现 [onCancel] 用于动画未完成时取消的恢复操作
         * */
        @TargetApi(Build.VERSION_CODES.Q)
        private inline fun setImeHeightListenerBelowQ(
            view: View,
            crossinline onPrepare: () -> Unit,
            crossinline onStart: () -> Unit,
            crossinline onEnd: () -> Unit,
            crossinline onProgress: (height: Int, fraction: Float, offset: Int) -> Unit
        ) {
            var pre = 0
            var anim: WindowInsetsAnimationCompat? = null
            val cb = object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                    super.onPrepare(animation)
                    anim = animation
                    animation.durationMillis
                    onPrepare()
                }

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    onStart()
                    return super.onStart(animation, bounds)
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    super.onEnd(animation)
                    onEnd()
                }

                override fun onProgress(insets: WindowInsetsCompat, animations: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
                    val height = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                    onProgress(height, anim?.fraction ?: 0f, height - pre)
                    pre = height
                    return insets
                }
            }
            ViewCompat.setWindowInsetsAnimationCallback(view, cb)
        }
    }
}

