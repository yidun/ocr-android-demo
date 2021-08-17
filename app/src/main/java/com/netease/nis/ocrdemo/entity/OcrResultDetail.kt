package com.netease.nis.ocrdemo.entity

data class OcrResultDetail(
    var ocrName: String?, var ocrCardNo: String?, var expireDate: String?,
    var gender: String?, var nation: String?, var birthday: String?,
    var address: String?, var authority: String?, var ocrAvatar: String?
) {

    override fun toString(): String {
        return "姓名：   $ocrName\r\n" +
                "性别：   $gender\r\n" +
                "民族：   $nation\r\n" +
                "出生：   $birthday\r\n" +
                "住址：   $address\r\n" +
                "证件号码：   $ocrCardNo\r\n" +
                "签发机关：   $authority\r\n" +
                "有效日期：   $expireDate\r\n"
    }
}