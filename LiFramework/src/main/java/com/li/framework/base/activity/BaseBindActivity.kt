package com.li.framework.base.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.li.framework.base.interfaces.IViewBinding

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/26
 */
abstract class BaseBindActivity<VB: ViewBinding>: BaseActivity(), IViewBinding<VB> {

    /**
     * 视图绑定
     * */
    protected open val binding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        initViewBinding(layoutInflater)
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBeforeSetContentView(savedInstanceState)
        setContentView(binding.root)
        onDefCreate(savedInstanceState)
    }

    /**
     * 在 [setContentView] 前调用
     * */
    protected open fun onBeforeSetContentView(savedInstanceState: Bundle?) {

    }

    /**
     * 在 [setContentView] 后调用， 替代默认的 [onCreate]
     * */
    abstract fun onDefCreate(savedInstanceState: Bundle?)




}