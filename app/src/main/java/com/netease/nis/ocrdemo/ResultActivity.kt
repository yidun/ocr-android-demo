package com.netease.nis.ocrdemo

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        val ocrDetail = intent?.getStringExtra("detail")
        ocrDetail?.let {
            val detailJson = JSONObject(it)
            val result = OcrResultDetail(
                detailJson.optString("ocrName"), detailJson.optString("ocrCardNo"),
                detailJson.optString("expireDate"), detailJson.optString("gender"),
                detailJson.optString("nation"), detailJson.optString("birthday"),
                detailJson.optString("address"), detailJson.optString("authority"),
                detailJson.optString("ocrAvatar")
            )
            val taskId = intent?.getStringExtra("taskId")
            tv_token.text = taskId
            tv_result_body.text = result.toString()
            tv_copy.setOnClickListener {
                val token: String = tv_token.text.toString()
                val clipData = ClipData.newPlainText("token", token)
                mClipboardManager?.setPrimaryClip(clipData)
                Util.showToast(this, "复制成功")
            }
            btn_back2home.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("from_result_activity", true)
                setResult(Activity.RESULT_OK, intent)
                startActivity(intent)
            }
        }

    }
}