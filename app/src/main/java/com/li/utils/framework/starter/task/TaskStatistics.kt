package com.li.utils.framework.starter.task

import android.util.Log
import java.util.concurrent.atomic.AtomicInteger

/**
 * Task 统计情况
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
object TaskStatistics {

    private var mCostTime = 0L

    val costTime: Long
        get() = mCostTime

    @Volatile
    private var mCurrentSituation = ""

    private var mBeans = mutableListOf<TaskStatisticsBean>()

    private var mTaskDoneCount = AtomicInteger()

    var currentSituation: String
        get() = mCurrentSituation
        set(value) {
            Log.i("TaskStatistics", mCurrentSituation)
            mCurrentSituation = value
            initStatistics()
        }

    fun markTaskDone() {
        mTaskDoneCount.getAndIncrement()
    }

    fun initStatistics() {
        val bean = TaskStatisticsBean(mCurrentSituation, mTaskDoneCount.get())
        mBeans.add(bean)
        mTaskDoneCount = AtomicInteger(0)
    }

}