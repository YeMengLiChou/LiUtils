package com.li.utils.framework.starter.task

import android.os.Process
import android.content.Context
import com.li.utils.framework.starter.dispatcher.DispatcherExecutor
import com.li.utils.framework.starter.dispatcher.TaskDispatcher
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

/**
 * 执行任务的基类
 * ```
 * class InitXxxTask(xxx) : Task() {
 *      //  异步线程执行的 Task 在被调用 await 的时候等待
 *      override fun needWait(): Boolean = true
 *
 *      override fun run() {
 *          // 需要执行的初始化逻辑
 *      }
 *
 *      // 依赖某些任务，在某些任务完成后才能执行
 *      override fun dependsOn(): MutableList<Class<out Task>> {
 *          // 把需要依赖的任务添加到列表
 *          return mutableListOf (
 *              InitXxxTask::class.java,
 *              ....
 *          )
 *      }
 *      // 指定执行的线程池，分为 CPU 和 IO, 默认为 IO
 *      override fun runOn(): ExecutorService? {
 *          return DispatcherExecutor.mIOExecutor
 *      }
 * }
 * ```
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
abstract class Task: ITask {
    companion object {
        private const val TAG = "Task"
    }

    protected var context: Context? = TaskDispatcher.context

    /**
     * 是否在主进程
     * */
    protected var mIsMainProcess: Boolean = TaskDispatcher.isMainProcess

    /**
     * 是否正在等待
     * */
    @Volatile
    var isWaiting = false

    /**
     * 是否正在执行
     * */
    @Volatile
    var isRunning = false

    /**
     * 是否已经执行完毕
     * */
    @Volatile
    var isFinished = false

    /**
     * 是否已经被分发
     * */
    @Volatile
    var isDispatched = false

    /**
     * 依赖的前置Task，用于进行线程同步
     * */
    private val mDependsCountDownLatch = CountDownLatch(
        dependOn?.size ?: 0
    )

    /**
     * 等待前置 Task 执行完毕
     * */
    fun waitToSatisfy() {
        try {
            mDependsCountDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 前置 Task 执行完毕一个
     * */
    fun satisfy() {
        mDependsCountDownLatch.countDown()
    }

    /**
     * 是否需要尽快执行，解决特殊场景的问题：
     * 一个Task耗时非常多但是优先级却一般，很有可能开始的时间较晚，导致最后只是在等它，这种可以早开始。
     * */
    fun needRunAsSoon(): Boolean {
        return false
    }

    /**
     * Task的优先级，运行在主进程则不需要更改优先级
     * */
    override fun priority(): Int {
        return Process.THREAD_PRIORITY_BACKGROUND
    }

    /**
     * 指定 Task 在哪个线程池执行，默认在 IO 线程池
     *
     * 可以切换成为 [cpuExecutor][DispatcherExecutor.cpuExecutor]
     * */
    override fun runOn(): ExecutorService? {
        return DispatcherExecutor.ioExecutor
    }

    /**
     * 前置 Task
     * */
    override val dependOn: List<Class<out Task>>?
        get() = null

    /**
     * 异步线程执行的 Task 是否需要在被调用 await 的时候等待，默认不需要
     * */
    override fun needWait(): Boolean = false

    /**
     * 默认在主进程中执行
     * */
    override fun onlyInMainProcess(): Boolean = true

    /**
     * 执行结束后的 Runnable
     * */
    override val tailRunnable: Runnable?
        get() = null

    /**
     *
     * */
    override fun needCallback(): Boolean = false
}