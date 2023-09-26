package com.li.framework.base.interfaces

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/26
 */
interface IViewBinding<VB: ViewBinding> {
    /**
     * 初始化 [ViewBinding]
     * @param layoutInflater
     * @param container fragment使用时需要传入
     * @return VB 的实例
     * */
    fun initViewBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup? = null
    ): VB
}