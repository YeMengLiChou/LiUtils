package com.li.utils.framework.util

import android.app.Application

/**
 * 尺寸单位转化工具类
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
object SizeUtils {

    private lateinit var appContext: Application

    private val displayMetrics get() = appContext.resources.displayMetrics

    /**
     * 初始化
     * */
    fun init(application: Application) {
        appContext = application
    }

    /**
     * dp 转 px
     * */
    fun dp2px(dpValue: Float): Float {
        return dpValue * displayMetrics.density
    }

    /**
     * px 转 dp
     * */
    fun px2dp(pxValue: Float): Float {
        return pxValue / displayMetrics.density
    }

    /**
     * sp 转 px
     * */
    fun sp2px(spValue: Float): Float {
        return spValue * displayMetrics.scaledDensity
    }

    /**
     * px 转 sp
     * */
    fun px2sp(pxValue: Float): Float {
        return pxValue / displayMetrics.scaledDensity
    }
}

