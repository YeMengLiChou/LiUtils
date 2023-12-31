package com.li.utils.framework.base.fragment

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
abstract class BaseMvvmFragment<VB: ViewBinding, VM: ViewModel>: BaseBindFragment<VB>(), IViewModel<VM> {
    /**
     * [VM] 对应实例
     * */
    protected open val viewModel: VM by lazy(mode = LazyThreadSafetyMode.NONE) {
        initViewModel()
    }

    /**
     * 默认使用 [androidx.lifecycle.SavedStateViewModelFactory] 作为默认 Factory
     */
    override fun getViewModelFactory(): ViewModelProvider.Factory = defaultViewModelProviderFactory

    /**
     * 传入 ViewModel 的一些额外信息，例如 Intent
     * */
    override fun getCreationExtras(): CreationExtras = defaultViewModelCreationExtras

    /**
     * 该方法获取的是与 Activity 相同实例的 ViewModel。如果需要重新维护，则重写该方法
     * */
    override fun initViewModel(): VM {
        return ViewModelProvider(requireActivity(), getViewModelFactory())[getViewModelClass()]
    }


}