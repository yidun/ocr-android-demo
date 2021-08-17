package com.netease.nis.ocrdemo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.netease.nis.ocrdemo.entity.OcrResultDetail
import com.netease.nis.ocrdemo.utils.Util
import kotlinx.android.synthetic.main.activity_result.*
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {
    private var mClipboardManager: ClipboardManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        mClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        initView()
    }

    private fun initView() {
        var ocrDetail = intent?.getStringExtra("detail")
        var detailJson = JSONObject(ocrDetail)
        var result = OcrResultDetail(
            detailJson.optString("ocrName"), detailJson.optString("ocrCardNo"),
            detailJson.optString("expireDate"), detailJson.optString("gender"),
            detailJson.optString("nation"), detailJson.optString("birthday"),
            detailJson.optString("address"), detailJson.optString("authority"),
            detailJson.optString("ocrAvatar")
        )
        var taskId = intent?.getStringExtra("taskId")
        tv_token.text = taskId
        tv_result_body.text = result.toString()
        tv_copy.setOnClickListener {
            val token: String = tv_token.text.toString()
            val clipData = ClipData.newPlainText("token", token)
            mClipboardManager?.setPrimaryClip(clipData)
            Util.showToast(this, "复制成功")
        }
        btn_back2home.setOnClickListener {
            var intent = Intent(ResultActivity@ this, MainActivity::class.java)
            intent.putExtra("from_result_activity", true)
            startActivity(intent)
        }
    }
}