package com.netease.nis.ocrdemo.utils

import android.app.Activity
import android.widget.Toast

object Util {
    /**
     * 设置当前窗口亮度
     *
     * @param brightness
     */
    @JvmStatic
    fun setWindowBrightness(context: Activity, brightness: Float) {
        val window = context.window
        val lp = window.attributes
        lp.screenBrightness = brightness
        window.attributes = lp
    }
}