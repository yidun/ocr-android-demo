package com.netease.nis.ocrdemo.utils

import android.app.Activity
import android.widget.Toast

object Util {
    fun showToast(activity: Activity, tip: String) {
        activity.runOnUiThread {
            Toast.makeText(activity.applicationContext, tip, Toast.LENGTH_LONG).show()
        }
    }
}