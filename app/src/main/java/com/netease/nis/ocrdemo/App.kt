package com.netease.nis.ocrdemo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * @author liuxiaoshuai
 * @date 2021/12/20
 * @desc
 * @email liulingfeng@mistong.com
 */
class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}