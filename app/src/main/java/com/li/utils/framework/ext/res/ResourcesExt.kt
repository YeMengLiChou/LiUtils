package com.li.utils.framework.ext.res

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.li.utils.LiUtilsApp


/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */
private val res: Resources
    get() = appContext.resources

private val appContext: Context
    get() = LiUtilsApp.application

/**
 * 获取 [colorId] 对应的 color值
 * */
fun Context.color(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

/**
 * 获取 [colorId] 对应的 color值
 * */
fun Fragment.color(@ColorRes colorId: Int) = ContextCompat.getColor(requireContext(), colorId)

/**
 * 获取[color]字符串对应的 color值
 * */
fun Context.color(color: String) = Color.parseColor(color)

/**
 * 获取[color]字符串对应的 color值
 * */
fun Fragment.color(color: String) = Color.parseColor(color)

/**
 * 获取 [id] 对应的字符串值
 * */
fun Context.string(@StringRes id: Int) = resources.getString(id)

/**
 * 获取 [id] 对应的字符串值
 * */
fun Fragment.string(@StringRes id: Int) = resources.getString(id)

/**
 * 获取 [id] 对应的数组
 * */
fun Context.stringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)

/**
 * 获取 [id] 对应的数组
 * */
fun Fragment.stringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)

/**
 * 获取 [id] 对应的 drawable
 * */
fun Context.drawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

/**
 * 获取 [id] 对应的 drawable
 * */
fun Fragment.drawable(@DrawableRes id: Int) = ContextCompat.getDrawable(appContext, id)

/**
 * 获取 [id] 对应的 dimen
 * */
fun Context.dimenPx(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

/**
 * 获取 [id] 对应的 dimen
 * */
fun Fragment.dimenPx(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

/**
 * 获取 [id] 对应的 integer值
 * */
fun Context.integer(@IntegerRes id: Int) = resources.getInteger(id)

/**
 * 获取 [id] 对应的 integer值
 * */
fun Fragment.integer(@IntegerRes id: Int) = resources.getInteger(id)

/**
 * 获取 [id] 对应的 animation
 * */
fun Context.animation(@AnimRes id: Int) = AnimationUtils.loadAnimation(this, id)!!

/**
 * 获取 [id] 对应的 animation
 * */
fun Fragment.animation(@AnimRes id: Int) = AnimationUtils.loadAnimation(requireContext(), id)!!

/**
 * 获取 [id] 对应的 animation
 * */
fun Context.boolean(@BoolRes id: Int) = resources.getBoolean(id)

/**
 * 获取 [id] 对应的 animation
 * */
fun Fragment.boolean(@BoolRes id: Int) = resources.getBoolean(id)

/**
 * 获取 [Context] 对应的 color值
 * */
inline val Context.layoutInflater: android.view.LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater

/**
 * 从 [layoutId] 的xml初始化View
 * */
fun inflateLayout(
    @LayoutRes layoutId: Int,
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): View {
    return appContext.layoutInflater.inflate(layoutId, parent, attachToParent)
}