package com.li.utils.framework.ext.common

import com.squareup.moshi.Moshi
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *
 * Moshi 扩展类
 * - 需要使用 [JsonClass] 生成相关的 JsonAdapter
 * @author Gleamrise
 * <br/>Created: 2023/10/04
 */

/**
 * moshi 实例
 * */
val moshi: Moshi by lazy {
    Moshi.Builder()
        .build()
}

/**
 * 转为 Json
 * @param indent pretty 就传入 "\t"
 * */
inline fun <reified T> T.toJson(indent: String = ""): String? {
    try {
        val jsonAdapter = moshi.adapter<T>(getGenericType<T>())
        return jsonAdapter.indent(indent).toJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * 从 Json 转为 对象实例
 * */
inline fun <reified T> String.fromJson(): T? {
    try {
        val jsonAdapter = moshi.adapter<T>(getGenericType<T>())
        return jsonAdapter.fromJson(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * 自定义的类，用来包装泛型
 * */
abstract class MoshiTypeReference<T>

/**
 * 获取通用类型
 * @
 * */
inline fun <reified T> getGenericType(): Type {
    return (object :
        MoshiTypeReference<T>() {}::class.java)
        .genericSuperclass
        .let { it as ParameterizedType }
        .actualTypeArguments
        .first()
}

