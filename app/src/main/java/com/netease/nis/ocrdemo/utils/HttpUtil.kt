package com.netease.nis.ocrdemo.utils

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

object HttpUtil {
    private val TAG = "Ocr.HttpUtil"
    private const val TIME_OUT_SECONDS: Long = 10
    private val sClient: OkHttpClient =
        OkHttpClient().newBuilder().connectionSpecs(
            listOf(
                ConnectionSpec.MODERN_TLS,
                ConnectionSpec.COMPATIBLE_TLS,
                ConnectionSpec.CLEARTEXT
            )
        ).readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS).build()

    fun upload(url: String, frontPicture: File?, backPicture: File?, callBack: ResponseCallBack) {
        object : Thread() {
            override fun run() {
                try {
                    upload2(url, frontPicture, backPicture, callBack)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "上传资源失败:$e")
                    callBack.onError(0, e.toString())
                }
            }
        }.start()
    }

    @Throws(Exception::class)
    private fun upload2(
        url: String,
        frontPicture: File?,
        backPicture: File?,
        callBack: ResponseCallBack
    ) {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        frontPicture?.let {
            builder.addFormDataPart(
                "frontPicture", "frontPicture",
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), frontPicture)
            )
        }
        backPicture?.let {
            builder.addFormDataPart(
                "backPicture", "backPicture",
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), backPicture)
            )
        }
        val requestBody: RequestBody = builder.build()
        val request = Request.Builder().url(url).post(requestBody).build()
        val response: Response = sClient.newCall(request).execute()
        if (response.isSuccessful) {
            callBack.onSuccess(response.body?.string())
        } else {
            callBack.onError(response.code, response.message)
        }
    }

    /**
     * 进行网络请求的回调
     */
    interface ResponseCallBack {
        fun onSuccess(result: String?)
        fun onError(errorCode: Int, msg: String?)
    }
}