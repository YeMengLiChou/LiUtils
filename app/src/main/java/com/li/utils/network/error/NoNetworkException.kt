package com.li.li_network.error

import java.io.IOException

/**
 * 无网络连接异常
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
open class NoNetworkException(
    val msg: String,
    e: Throwable? = null
): IOException(e)