package com.li.utils.framework.ext.common

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/04
 */
/**
 * 类型转换，转换失败为 null
 * */
inline fun <reified T> Any.asOrNull(): T? {
    return this as? T
}

/**
 * 类型转换，转换失败抛出异常
 * */
inline fun <reified T> Any.asNotNull(): T {
    return this as T
}

