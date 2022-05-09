package com.netease.nis.ocrdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.netease.nis.ocr.OcrCropListener
import com.netease.nis.ocr.OcrScanner
import com.netease.nis.ocrdemo.manager.BroadcastDispatcher
import com.netease.nis.ocrdemo.manager.showToast
import com.netease.nis.ocrdemo.utils.TipDialog
import com.netease.nis.ocrdemo.utils.Util
import kotlinx.android.synthetic.main.activity_ocr_scan.*

class OcrScanActivity : AppCompatActivity() {
    val TAG = "OCR_SDK"
    val tipDialog: TipDialog by lazy {
        TipDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setWindowBrightness(this, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_ocr_scan)
        BroadcastDispatcher.registerScreenOff(this)
        initView()
        initListener()
    }

    private fun initView() {
        val context = this
        val isBackOcr = "back" == intent?.getStringExtra("scan_type")
        if (isBackOcr) {
            OcrScanner.getInstance().setScanType(OcrScanner.SCAN_TYPE_NATIONAL_EMBLEM)
            tv_scan_type.text = "国徽面拍摄"
            tv_scan_type_desc.text = "  将身份证国徽面放入采集框中"
        } else {
            OcrScanner.getInstance().setScanType(OcrScanner.SCAN_TYPE_AVATAR)
            tv_scan_type.text = "人像面拍摄"
            tv_scan_type_desc.text = "  将身份证人像面放入采集框中"
        }
        OcrScanner.getInstance()
            .init(applicationContext, ocr_view, "易盾业务id")
        OcrScanner.getInstance().setCropListener(object : OcrCropListener {
            override fun onSuccess(picturePath: String?) {
                val intent = Intent(context, MainActivity::class.java)
                if (isBackOcr) {
                    intent.putExtra("back_pic_path", picturePath)
                } else {
                    intent.putExtra("frontal_pic_path", picturePath)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
                Log.e(TAG, "保存的图片路径为$picturePath")
            }

            override fun onError(code: Int, msg: String?) {
                "onError:$msg".showToast(applicationContext)
                Log.e(TAG, "ocr扫描出错，原因:$msg")
            }

            override fun onOverTime() {
                runOnUiThread {
                    tipDialog.setOwnerActivity(this@OcrScanActivity)
                    tipDialog.show()
                }
                Log.e(TAG, "ocr扫描检测超时")
            }
        })
        OcrScanner.getInstance().setTimeOut(1000 * 30)
        ocr_view?.postDelayed({
            OcrScanner.getInstance().start()
        }, 500)
    }

    private fun initListener() {
        iv_back.setOnClickListener { finish() }
        BroadcastDispatcher.addScreenStatusChangedListener(listener)
    }

    override fun onDestroy() {
        Util.setWindowBrightness(this, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
        OcrScanner.getInstance().stop()
        OcrScanner.getInstance().destroy()
        BroadcastDispatcher.unRegisterScreenOff(this)
        super.onDestroy()
    }

    private val listener = object : BroadcastDispatcher.ScreenStatusChangedListener {
        override fun onForeground() {
            runOnUiThread {
                ocr_view?.postDelayed({
                    OcrScanner.getInstance().start()
                }, 500)
            }
        }

        override fun onBackground() {
            OcrScanner.getInstance().stop()
        }
    }
}
