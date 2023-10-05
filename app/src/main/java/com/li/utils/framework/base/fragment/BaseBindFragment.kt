package com.li.utils.framework.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.li.utils.framework.base.interfaces.IViewBinding

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/26
 */
abstract class BaseBindFragment<VB: ViewBinding>: BaseFragment(), IViewBinding<VB> {
    /**
     * [onCreateView] 传入的 ViewContainer
     * */
    private var mViewContainer: ViewGroup? = null

    /**
     * [onCreateView] 传入的 LayoutInflater
     * */
    private var mLayoutInflater: LayoutInflater? = null

    /**
     * Fragment 对应的视图绑定实例
     * */
    protected open val binding by lazy(mode = LazyThreadSafetyMode.NONE) {
        require(mLayoutInflater != null) { "Don't use binding before `onDefCreateView`!" }
        initViewBinding(mLayoutInflater!!, mViewContainer)
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onDefCreateView(inflater, container, savedInstanceState)
        mViewContainer = container
        mLayoutInflater = inflater
        return binding.root
    }

    /**
     * 替代 [onCreateView]
     * */
    protected abstract fun onDefCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)


}