package com.netease.nis.ocrdemo.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.netease.nis.ocrdemo.MainActivity
import com.netease.nis.ocrdemo.R
import kotlinx.android.synthetic.main.dialog_tips.*

class TipDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tips)
        initView()
    }

    private fun initView() {
        iv_close.setOnClickListener {
            dismiss()
            jump2MainActivity()
        }
        btn_back.setOnClickListener {
            jump2MainActivity()
        }
    }

    private fun jump2MainActivity() {
        ownerActivity?.finish()
    }
}