package com.li.utils

import android.app.Application
import android.widget.Toast
import com.li.utils.framework.manager.DeviceInfoManager
import com.li.utils.framework.util.SizeUtils
import com.li.utils.framework.util.bar.SystemBarUtils
import kotlin.math.abs
import kotlin.system.exitProcess

/**
 * 需要调用 [init] 初始化该 [LiUtilsApp]
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/04
 */
object LiUtilsApp {

    /**
     * 全局 Application
     * */
    lateinit var application: Application
        private set

    /**
     * 是否处于 **调试模式**
     * */
    var isDebug = false
        private set

    /**
     * 上一次退出的时间
     * */
    private var lastExitTime = -1L

    /**
     * 初始化 LiUtilsApp
     * */
    fun init(application: Application, isDebug: Boolean) {
        LiUtilsApp.application = application
        LiUtilsApp.isDebug = isDebug
        // 初始化
        DeviceInfoManager.init(application)
        SizeUtils.init(application)
        SystemBarUtils.init(application)
    }

    /**
     * 二次确定退出
     * @param interval 确定退出的时间间隔（ms)
     * @param confirmAction 需要确认的逻辑，比如展示 Toast 提示等
     * @param beforeExit 退出前的逻辑，比如保存数据等
     * */
    fun exitConfirmed(
        interval: Long = 1500L,
        confirmAction: (() -> Unit)? = null,
        beforeExit: (() -> Unit)? = null
    ) {
        val currentTime = System.currentTimeMillis()
        if (abs(currentTime - lastExitTime) >= interval) {
            lastExitTime = currentTime
            confirmAction?.invoke() ?: run {
                Toast.makeText(application, "再返回一次退出", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 退出 App
            beforeExit?.invoke()
            exitProcess(0)
        }
    }
}