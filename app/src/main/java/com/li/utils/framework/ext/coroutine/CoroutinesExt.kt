package com.li.utils.framework.ext.coroutine

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/04
 */

/**
 * 主线程 [launch] 启动协程
 * */
fun CoroutineScope.launchMain(
    block: suspend CoroutineScope.() -> Unit
) = launch(Dispatchers.Main, block = block)

/**
 * IO线程 [launch] 启动协程
 * */
fun CoroutineScope.launchIO(
    block: suspend CoroutineScope.() -> Unit
) = launch(Dispatchers.IO, block = block)

/**
 * Default线程 [launch] 启动协程
 * */
fun CoroutineScope.launchDefault(
    block: suspend CoroutineScope.() -> Unit
) = launch(Dispatchers.Default, block = block)

/**
 * 主线程 [withContext] 启动协程
 * */
suspend fun <T> withMain(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.Main, block = block)


/**
 * IO线程 [withContext] 启动协程
 * */
suspend fun <T> withIO(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.IO, block = block)

/**
 * Default线程 [withContext] 启动协程
 * */
suspend fun <T> withDefault(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.Default, block = block)

/**
 * Main线程 [async] 启动协程
 * */
fun <T> CoroutineScope.asyncMain(
    block: suspend CoroutineScope.() -> T
) = async(Dispatchers.Main, block = block)

/**
 * IO线程 [async] 启动协程
 * */
fun <T> CoroutineScope.asyncIO(
    block: suspend CoroutineScope.() -> T
) = async(Dispatchers.IO, block = block)

/**
 * Default线程 [async] 启动协程
 * */
fun <T> CoroutineScope.asyncDefault(
    block: suspend CoroutineScope.() -> T
) = async(Dispatchers.Default, block = block)

/**
 * Activity/Fragment 主线程 [launch] 启动协程
 * */
fun LifecycleOwner.launchMain (
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(Dispatchers.Main, block = block)

/**
 * Activity/Fragment IO线程 [launch] 启动协程
 * */
fun LifecycleOwner.launchIO(
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(Dispatchers.IO, block = block)

/**
 * Activity/Fragment Default线程 [launch] 启动协程
 * */
fun LifecycleOwner.launchDefault(
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(Dispatchers.Default, block = block)


/**
 * Activity/Fragment Main线程 [async] 启动协程
 * */
fun <T> LifecycleOwner.asyncMain(
    block: suspend CoroutineScope.() -> T
) = lifecycleScope.async(Dispatchers.Main, block = block)

/**
 * Activity/Fragment IO线程 [async] 启动协程
 * */
fun <T> LifecycleOwner.asyncIO(
    block: suspend CoroutineScope.() -> T
) = lifecycleScope.async(Dispatchers.IO, block = block)

/**
 * Activity/Fragment Default线程 [async] 启动协程
 * */
fun <T> LifecycleOwner.asyncDefault(
    block: suspend CoroutineScope.() -> T
) = lifecycleScope.async(Dispatchers.Default, block = block)

/**
 * 启动生命周期协程，在 onCreate 后，在 onStop 前调用
 *
 * Created state for a LifecycleOwner. For an [android.app.Activity], this state
 * is reached in two cases:
 *
 *  * after [onCreate][android.app.Activity.onCreate] call;
 *  * **right before** [onStop][android.app.Activity.onStop] call.
 *
 */
fun LifecycleOwner.launchOnCreated(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(context) {
    repeatOnLifecycle(Lifecycle.State.CREATED, block)
}



/**
 * 启动生命周期协程，在 onStarted 后，在 onPause 前调用
 *
 * For an [android.app.Activity], this state
 * is reached in two cases:
 *
 *  * after [onStart][android.app.Activity.onStart] call;
 *  * **right before** [onPause][android.app.Activity.onPause] call.
 *
 */
fun LifecycleOwner.launchOnStarted(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(context) {
    repeatOnLifecycle(Lifecycle.State.STARTED, block)
}


/**
 * 启动生命周期协程，在 onResumed 后面调用
 *
 * For an [android.app.Activity], this state
 * is reached after [onResume][android.app.Activity.onResume] is called.
 */
fun LifecycleOwner.launchOnResume(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(context) {
    repeatOnLifecycle(Lifecycle.State.RESUMED, block)
}


/**
 * 启动生命周期协程，在 onDestroy 前调用
 *
 * After this event, this Lifecycle will not dispatch
 * any more events. For instance, for an [android.app.Activity], this state is reached
 * **right before** Activity's [onDestroy][android.app.Activity.onDestroy] call.
 */
fun LifecycleOwner.launchDestroyed(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(context) {
    repeatOnLifecycle(Lifecycle.State.DESTROYED, block)
}


/**
 * 启动生命周期协程，在 onCreate 前调用
 *
 * For an [android.app.Activity], this is
 * the state when it is constructed but has not received
 * [onCreate][android.app.Activity.onCreate] yet.
 */
fun LifecycleOwner.launchOnInitialized(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch(context) {
    repeatOnLifecycle(Lifecycle.State.INITIALIZED, block)
}

