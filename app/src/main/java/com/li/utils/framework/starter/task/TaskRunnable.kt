package com.li.utils.framework.starter.task

import android.os.Looper
import android.os.Process
import android.os.Trace
import android.util.Log
import com.li.utils.LiUtilsApp
import com.li.utils.framework.starter.dispatcher.TaskDispatcher

/**
 *
 * 负责执行 [Task] 的 [Runnable]
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class TaskRunnable(
    private val task: Task,
    private var dispatcher: TaskDispatcher?
): Runnable {

    companion object {
        private const val TAG = "TaskRunnable"
    }

    constructor(task: Task): this(task, null)


    override fun run() {
        val taskName = task::class.simpleName.toString()
        // 可以用于测试
        if (LiUtilsApp.isDebug) {
            Trace.beginSection(taskName)
        }
        Log.i(TAG, "---> TaskRunnable start: $taskName")
        Process.setThreadPriority(task.priority())
        var startTime = System.currentTimeMillis()

        // 等待前置 Task 执行完毕
        task.isWaiting = true
        task.waitToSatisfy()
        val waitTime = System.currentTimeMillis() - startTime

        // 开始执行 task
        startTime = System.currentTimeMillis()
        task.isRunning = true
        task.run()
        // 执行结束后
        task.tailRunnable?.run()
        // 已经结束完毕
        if (!task.needCallback() || !task.runOnMainThread()) {
            printTaskLog(startTime, waitTime)
            TaskStatistics.markTaskDone()
            task.isFinished = true
            dispatcher?.apply {
                notifyNext(task)
                markTaskDone(task)
            }
            Log.i(TAG, "<--- TaskRunnable finish: $taskName")
        }

        if (LiUtilsApp.isDebug) {
            Trace.endSection()
        }
    }

    /**
     * 打印出来Task执行的日志
     *
     * @param startTime
     * @param waitTime
     */
    private fun printTaskLog(startTime: Long, waitTime: Long) {
        val runTime = System.currentTimeMillis() - startTime
        if (LiUtilsApp.isDebug) {
            Log.i(
                TAG,
                """
                ${task.javaClass.simpleName}
                    | wait-time: $waitTime ms
                    | run-time: $runTime ms
                    | total-time: ${waitTime + runTime} ms
                    | is-main-thread: ${Looper.getMainLooper() == Looper.myLooper()}
                    | need-wait: ${(task.needWait() || Looper.getMainLooper() == Looper.myLooper())}
                    | thread-it: ${Thread.currentThread().id}
                    | thread-name: ${Thread.currentThread().name}
                    | situation: ${TaskStatistics.currentSituation}
                """.trimIndent()
            )
        }
    }
}