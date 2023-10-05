package com.li.utils.framework.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * 剪切板工具类
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/21
 */
object ClipboardUtils {

    /**
     * 复制字符串
     * @param context
     * @param copyString 复制的字符串
     * @param label 标签
     * */
    fun copyString(context: Context, copyString: String, label: String = "copy") {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(label, copyString)
        clipboardManager.setPrimaryClip(clip)
    }
}