package com.li.utils.framework.starter.task

import android.os.Process
import androidx.annotation.IntRange
import java.util.concurrent.Executor

/**
 * 任务接口
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
interface ITask {

    /**
     * 任务优先级
     * */
    @IntRange(
        from = Process.THREAD_PRIORITY_FOREGROUND.toLong(),
        to = Process.THREAD_PRIORITY_LOWEST.toLong()
    )
    fun priority(): Int


    /**
     * Task 的执行逻辑
     * */
    fun run()

    /**
     * 指定 Task 的线程池
     * */
    fun runOn(): Executor?

    /**
     * 是否在主线程中执行
     * @return true 为在主线程执行
     * */
    fun runOnMainThread(): Boolean

    /**
     * 依赖前面的 Task，需要前面的 Task 执行完毕才执行该 Task
     * */
    val dependOn: List<Class<out ITask>>?

    /**
     * 是否需要等待执行
     * */
    fun needWait(): Boolean

    /**
     * 是否只在主进程中执行
     * */
    fun onlyInMainProcess(): Boolean

    /**
     * 当前 Task 执行后的所执行 Runnable
     * */
    val tailRunnable: Runnable?

    /**
     * 设置任务执行完毕的回调
     * */
    fun setTaskCallback(callback: TaskCallback)

    /**
     * 是否需要回调
     * */
    fun needCallback(): Boolean




}