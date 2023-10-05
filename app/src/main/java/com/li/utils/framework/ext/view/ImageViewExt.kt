package com.li.utils.framework.ext.view

import android.widget.ImageView

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/07
 */

/**
 * 设置偏移量 [[deltaX], [deltaY]]
 * */
fun ImageView.imageTranslate(deltaX: Float, deltaY: Float): ImageView {
    this.imageMatrix.setTranslate(deltaX, deltaY)
    return this
}