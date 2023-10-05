package com.li.utils.framework.starter.dispatcher

import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import com.li.utils.framework.starter.task.Task
import com.li.utils.framework.starter.task.TaskCallback
import com.li.utils.framework.starter.task.TaskRunnable
import com.li.utils.framework.starter.task.TaskStatistics
import com.li.utils.framework.starter.util.TaskUtils
import java.lang.StringBuilder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
class TaskDispatcher private constructor() {
    companion object {
        private const val TAG = "TaskDispatcher"

        /**
         * 最大等待时间
         * */
        private const val WAIT_TIME = 10 * 1000L

        var context: Application? = null
            private set

        /**
         * 是否在 **主进程** 执行
         * */
        var isMainProcess  = false
            private set

        /**
         * 是否已经初始完毕，需要调用 [init]
         * */
        @Volatile
        private var mInitialized = false

        /**
         * 初始化该 TaskDispatcher
         * */
        fun init(context: Application) {
            Companion.context = context
            mInitialized = true
            isMainProcess = TaskUtils.isMainProcess(context)
            Log.i(TAG, "TaskDispatcher initialized!")
        }

        /**
         * 创建新实例
         * */
        fun newInstance(): TaskDispatcher {
            check(mInitialized) {
                "You must call `TaskDispatcher.init()` to initialize TaskDispatcher!"
            }
            return TaskDispatcher()
        }
    }

    /**
     * TaskDispatcher 分发任务开始时间
     * */
    private var mStartTime: Long = 0

    /**
     *  异步执行的 Task
     *  */
    private val mFutures: MutableList<Future<*>> = mutableListOf()

    /**
     *  所有需要执行的 Task
     *  */
    private var mAllTasks: MutableList<Task> = mutableListOf()

    /**
     *  所有需要执行的 Task 的 javaClass
     *  */
    private val mAllTaskClasses: MutableList<Class<out Task>> = mutableListOf()

    /**
     *  需要在主线程中执行的 Task
     *  */
    @Volatile
    private var mMainThreadTasks: MutableList<Task> = mutableListOf()

    /**
     *  记录还需执行的任务数
     *  */
    private var mCountDownLatch: CountDownLatch? = null

    /**
     *  记录需要 [Task.isWaiting] 的 Task 的数量
     *  */
    private val mNeedWaitCount = AtomicInteger()

    /**
     *  调用了 [await] 时还没结束的且需要等待的 Task
     *  */
    private val mNeedWaitTasks: MutableList<Task> = mutableListOf()

    /**
     * 已经结束了的Task
     * */
    @Volatile
    private var mFinishedTasks: MutableList<Class<out Task>> = ArrayList(100)

    /**
     * 被依赖的 Task 的后置 Task
     * */
    private val mDependedHashMap = HashMap<Class<out Task>, MutableList<Task>?>()

    /**
     *  启动器分析的次数，统计分析的耗时
     *  */
    private val mAnalyseCount = AtomicInteger()


    /**
     * 添加任务, 支持链式调用
     * */
    fun addTask(task: Task): TaskDispatcher {
        // 前置 Task 统计
        collectDepends(task)
        mAllTasks.add(task)
        mAllTaskClasses.add(task.javaClass)
        // 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
        if (isNeedWait(task)) {
            mNeedWaitTasks.add(task)
            mNeedWaitCount.getAndIncrement()
        }
        return this
    }

    /**
     * 调度 Task 并开始执行
     * */
    @MainThread
    fun start(): TaskDispatcher {
        mStartTime = System.currentTimeMillis()
        // 需要是主线程
        check(Looper.getMainLooper() == Looper.myLooper()) {
            "`TaskDispatcher.start()` must be called from MainThread"
        }

        if (mAllTasks.isNotEmpty()) {
            mAnalyseCount.getAndIncrement()
            printDependedMsg(true)
            // 通过拓扑排序得到 Task 的执行顺序
            mAllTasks = TaskUtils.sortTasks(mAllTasks, mAllTaskClasses).toMutableList()
            mCountDownLatch = CountDownLatch(mNeedWaitCount.get())
            dispatchAndExecuteAsyncTasks()
            Log.i(TAG,"Task analyse cost time begin `executeTaskMain`: ${(System.currentTimeMillis() - mStartTime)} ms")
            executeTaskMain()
        }
        Log.i(TAG,"Task analyse cost time `start`: ${(System.currentTimeMillis() - mStartTime)} ms")
        return this
    }


    /**
     * 等待 Task 都被执行完毕
     * */
    @MainThread
    fun await() {
        try {
            Log.i(TAG,"TaskDispatcher waiting tasks count: ${mNeedWaitCount.get()}")
            for (task in mNeedWaitTasks) {
                Log.i(TAG,"\tNeedWait: ${task.javaClass.simpleName}")
            }
            if (mNeedWaitCount.get() > 0) {
                mCountDownLatch?.await(WAIT_TIME, TimeUnit.MILLISECONDS)
            }
            Log.i(TAG, "TaskDispatcher `await` cost: ${System.currentTimeMillis() - mStartTime} ms")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    /**
     * 直接执行指定 [task]
     * */
    fun executeTask(task: Task) {
        if (isNeedWait(task)) {
            mNeedWaitCount.getAndDecrement()
        }
        task.runOn()?.execute(TaskRunnable(task, this))
    }


    /**
     * 取消所有准备执行/正在执行的任务
     * */
    fun cancel() {
        mFutures.forEach {
            it.cancel(true)
        }
    }


    /**
     * 标记某任务已经完成
     * */
    fun markTaskDone(task: Task) {
        if (isNeedWait(task)) {
            mFinishedTasks.add(task.javaClass)
            mNeedWaitTasks.remove(task)
            mCountDownLatch?.countDown()
            mNeedWaitCount.getAndDecrement()
        }
    }

    /**
     * 通知后续 Task 该前置 [launchTask] 已经完成
     * */
    fun notifyNext(launchTask: Task) {
        val arrayList = mDependedHashMap[launchTask.javaClass]
        if (!arrayList.isNullOrEmpty()) {
            for (task in arrayList) {
                task.satisfy()
            }
        }
    }

    /**
     * 统计该 [task] 的前置依赖
     * */
    private fun collectDepends(task: Task) {
        // 对于每一个前置依赖 Task，将其添加到 mDependedHashMap
        task.dependOn?.forEach {
            if (mDependedHashMap[it] == null) {
                mDependedHashMap[it] = mutableListOf()
            }
            mDependedHashMap[it]?.add(task)
            if (mFinishedTasks.contains(it)) {
                task.satisfy()
            }
        }
    }

    /**
     * 判断指定 [task] 是否需要等待
     * - task 不在主线程运行
     * - task 依赖于前置任务
     * */
    private fun isNeedWait(task: Task): Boolean =
        !task.runOnMainThread() && task.needWait()


    /**
     * 调度，执行 **异步** 任务
     *
     * */
    private fun dispatchAndExecuteAsyncTasks() {
        for (task in mAllTasks) {
            // 应用程序一般是主进程，当前也是主进程，因此这里一般执行不到
            if (task.onlyInMainProcess() && !isMainProcess) {
                markTaskDone(task)
            } else {
                // 分发到指定的线程池执行
                dispatchTaskReal(task)
            }
            task.isDispatched = true
        }
    }

    /**
     * 调度到主线程或者其他线程池
     * */
    private fun dispatchTaskReal(task: Task) {
        // 如果是需要在主线程中运行
        if (task.runOnMainThread()) {
            mMainThreadTasks.add(task)
            if (task.needCallback()) {
                task.setTaskCallback(object : TaskCallback {
                    override fun callback() {
                        TaskStatistics.markTaskDone()
                        task.isFinished = true
                        notifyNext(task) // 通知后续任务已经完成
                        markTaskDone(task)
                        // TODO: log current task
                    }
                })
            }
        } else {
            // 直接提交到线程池
            val future = task.runOn()?.submit(TaskRunnable(task, this))
            future?.let {
                mFutures.add(it)
            }
        }
    }

    /**
     * 在主线程执行 Task
     * */
    private fun executeTaskMain() {
        mStartTime = System.currentTimeMillis()
        Log.i(TAG, "===>MainThread task count: ${mMainThreadTasks.size}")
        for (task in mMainThreadTasks) {
            val time = System.currentTimeMillis()
            TaskRunnable(task, this).run()
            Log.i(TAG, "\tMainThread task ${task.javaClass.simpleName} cost: ${(System.currentTimeMillis() - time)} ms")
        }
        Log.i(TAG,"<===MainThread Task total cost: ${(System.currentTimeMillis() - mStartTime)} ms")
    }


    private fun printDependedMsg(isPrintAllTask: Boolean) {
        Log.i(TAG, "Depends: ")
        Log.i(TAG,"\tNeedWait task count: ${mNeedWaitCount.get()}")
        if (isPrintAllTask) {
            for (cls in mDependedHashMap.keys) {
                Log.i(TAG, "\t\tDepended task: ${cls.simpleName} ${mDependedHashMap[cls]?.size}")
                mDependedHashMap[cls]?.let {
                    val sb = StringBuilder()
                    for (task in it) {
                        sb.append("\t\t\t${task::class.simpleName}\n")
                    }
                    Log.i(TAG, sb.toString())
                }
            }
        }
    }





}