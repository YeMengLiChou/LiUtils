package com.li.utils.framework.helper

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

/**
 * 用于监听App的前后台切换
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
object AppFrontBackHelper {

    /**
     * 前后台切换监听接口
     * */
    interface AppFrontBackListener {
        /**
         * 当 App 切换到 **前台** 时触发
         * */
        fun onAppFront()

        /**
         * 当 App 切换到 **后台** 时触发
         * */
        fun onAppBack()
    }

    /**
     * 已经经过 [onStart][android.app.Activity.onStart] 的 Activity 的个数
     * */
    private var mOnStartedActivityCount = 0

    /**
     * 注册了监听接口的
     * */
    private val listeners = mutableListOf<AppFrontBackListener>()

    /**
     * 全局只需存在一个用于统计 onStart 的 Activity 的回调
     * */
    private var mLifecycleCallbacks: ActivityLifecycleCallbacks? = null

    /**
     * 注册前后台切换的监听
     * */
    fun register(
        application: Application,
        listener: AppFrontBackListener
    ) {
        // 初始化
        if (mLifecycleCallbacks == null) {
            initActivityLifecycleCallbacks()
            application.registerActivityLifecycleCallbacks(mLifecycleCallbacks)
        }
        listeners.add(listener)
    }

    /**
     * 取消注册前后台切换的监听
     * */
    fun unregister(
        application: Application,
        listener: AppFrontBackListener
    ) {
        if (listeners.size == 1) {
            application.unregisterActivityLifecycleCallbacks(mLifecycleCallbacks)
            mLifecycleCallbacks = null
        }
        listeners.remove(listener)
    }

    /**
     * 初始化 [mLifecycleCallbacks]，当 [mLifecycleCallbacks] 为空时才调用
     * */
    private fun initActivityLifecycleCallbacks() {
        mLifecycleCallbacks = object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            // app 进入前台时，activity的生命周期会从 onStop 转为 onStart
            override fun onActivityStarted(activity: Activity) {
                mOnStartedActivityCount += 1
                if (mOnStartedActivityCount == 1) {
                    listeners.forEach { it.onAppFront() }
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            // app 进入后台时，activity的生命状态会进入 onStop
            override fun onActivityStopped(activity: Activity) {
                mOnStartedActivityCount -= 1
                if (mOnStartedActivityCount == 0) {
                    listeners.forEach { it.onAppBack() }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        }
    }

}