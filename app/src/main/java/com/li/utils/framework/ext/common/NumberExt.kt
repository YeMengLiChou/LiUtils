package com.li.utils.framework.ext.common

import com.li.utils.framework.util.SizeUtils

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */

/**
 * dp 整型单位
 * */
val Number.dp: Int
    get() = (SizeUtils.dp2px(this.toFloat()) + 0.5f).toInt()

/**
 * sp 整型单位
 * */
val Number.sp: Int
    get() = SizeUtils.sp2px(this.toFloat() + 0.5f).toInt()

/**
 * dp 浮点型单位
 * */
val Number.dpf: Float
    get() = SizeUtils.dp2px(this.toFloat())

/**
 * sp 浮点型单位
 * */
val Number.spf: Float
    get() = SizeUtils.sp2px(this.toFloat())