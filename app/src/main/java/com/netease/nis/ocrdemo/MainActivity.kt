package com.netease.nis.ocrdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.netease.nis.ocrdemo.utils.HttpUtil
import com.netease.nis.ocrdemo.utils.Util
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class MainActivity : AppCompatActivity() {
    val OCR_CHECK_URL = "请用易盾官网上的URL"
    var frontalPic: String? = null
    var backPic: String? = null
    var pictureCount = 0

    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    private fun initView() {
        ll_avatar.setOnClickListener {
            jump2OcrScanActivity("front")
        }
        ll_emblem.setOnClickListener {
            jump2OcrScanActivity("back")
        }
        btn_start_scan.setOnClickListener {
            val frontalPicFile = frontalPic?.let { File(it) }
            val backPicFile = backPic?.let { File(it) }
            val context = this
            HttpUtil.upload(OCR_CHECK_URL, frontalPicFile, backPicFile,
                object : HttpUtil.ResponseCallBack {
                    override fun onSuccess(result: String?) {
                        result?.let {
                            val jsonResponse = JSONObject(it)
                            if (jsonResponse.optInt("code") == 200) {
                                val jsonResult = jsonResponse.optJSONObject("result")
                                val detail = jsonResult?.optString("ocrResponseDetail")
                                detail?.let {
                                    if (detail != "null") {
                                        val taskId = jsonResult.optString("taskId")
                                        val intent = Intent(context, ResultActivity::class.java)
                                        intent.putExtra("detail", detail)
                                        intent.putExtra("taskId", taskId)
                                        startActivity(intent)
                                        return
                                    }
                                }
                            }
                        }
                        Util.showToast(this@MainActivity, "OCR检测失败，请稍后重试")
                    }

                    override fun onError(errorCode: Int, msg: String?) {
                        Util.showToast(this@MainActivity, "OCR检测图片上传失败，原因$msg")
                    }
                })
        }
    }

    private fun updateUi(intent: Intent) {
        pictureCount = 0
        if (intent.hasExtra("back_pic_path")) {
            val backPicPath = intent.getStringExtra("back_pic_path")
            backPic = backPicPath
        }

        if (intent.hasExtra("frontal_pic_path")) {
            val frontalPicPath = intent.getStringExtra("frontal_pic_path")
            frontalPic = frontalPicPath
        }

        if (intent.hasExtra("from_result_activity")) {
            if (intent.getBooleanExtra("from_result_activity", false)) {
                iv_emblem.setImageResource(R.mipmap.pic_emblem_2x)
                tv_emblem_retry_upload.visibility = View.GONE
                iv_avatar.setImageResource(R.mipmap.pic_avatar_2x)
                tv_avatar_retry_upload.visibility = View.GONE
                frontalPic = null
                backPic = null
            }
        }

        if (!TextUtils.isEmpty(backPic)) {
            Glide.with(this).load(backPic)
                .apply(RequestOptions().signature(ObjectKey(System.currentTimeMillis())))
                .into(iv_emblem)
            tv_emblem_retry_upload.visibility = View.VISIBLE
            tv_emblem_retry_upload.setOnClickListener {
                jump2OcrScanActivity("back")
            }
            pictureCount++
        }
        if (!TextUtils.isEmpty(frontalPic)) {
            Glide.with(this).load(frontalPic)
                .apply(RequestOptions().signature(ObjectKey(System.currentTimeMillis())))
                .into(iv_avatar)
            tv_avatar_retry_upload.visibility = View.VISIBLE
            tv_avatar_retry_upload.setOnClickListener {
                jump2OcrScanActivity("front")
            }
            pictureCount++
        }
        btn_start_scan.isEnabled = pictureCount > 0
    }

    private fun jump2OcrScanActivity(scanType: String) {
        if (!EasyPermissions.hasPermissions(this@MainActivity, Manifest.permission.CAMERA)) {
            Toast.makeText(applicationContext, "您未授予相机权限，请到设置中开启权限", Toast.LENGTH_LONG).show()
        } else if (!EasyPermissions.hasPermissions(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(applicationContext, "您未授予文件存储权限，请到设置中开启权限", Toast.LENGTH_LONG)
                .show()
        } else {
            val intent = Intent(this, OcrScanActivity::class.java)
            intent.putExtra("scan_type", scanType)
            startActivityForResult(intent, 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults, this
        )
    }


    @AfterPermissionGranted(RC_ALL_PERM)
    private fun requestPermissions() {
        if (!EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            EasyPermissions.requestPermissions(
                this, getString(R.string.permission_tip),
                RC_ALL_PERM, *PERMISSIONS
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                updateUi(it)
            }
        }
    }
}

private const val RC_ALL_PERM = 10000