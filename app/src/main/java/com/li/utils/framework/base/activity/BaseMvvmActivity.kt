package com.li.utils.framework.base.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.viewbinding.ViewBinding
import com.li.utils.framework.base.interfaces.IViewModel

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/26
 */
abstract class BaseMvvmActivity<VB : ViewBinding, VM : ViewModel>
    : BaseBindActivity<VB>(), IViewModel<VM> {
    /**
     * 该 Activity 对应的 [ViewModel] 实例
     * */
    private val viewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        initViewModel()
    }

    override fun initViewModel(): VM {
        return ViewModelProvider(this, getViewModelFactory())[getViewModelClass()]
    }

    /**
     * 使用默认的 ViewModel 工厂 [androidx.lifecycle.SavedStateViewModelFactory]
     *
     * 如果 ViewModel 的构造函数有自定义参数，则需要自己写一个 Factory，然后在这里返回即可
     * */
    override fun getViewModelFactory(): ViewModelProvider.Factory = defaultViewModelProviderFactory


    /**
     * 传入 ViewModel 的一些额外信息，例如 Intent
     * */
    override fun getCreationExtras(): CreationExtras = defaultViewModelCreationExtras

}