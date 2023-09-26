package com.li.framework.base.recyclerview

import androidx.viewbinding.ViewBinding

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/09/26
 */
open class BaseBindViewHolder<VB: ViewBinding>(
    val binding: VB
): BaseViewHolder(binding.root)