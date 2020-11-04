package com.labijie.application.thridparty.wechat

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

open class WechatResponse {
    companion object{
        const val RETURN_CODE_SUCCESS = "SUCCESS"
        const val RESULT_CODE_SUCCESS = "SUCCESS"
    }

    @JacksonXmlProperty(localName = "return_code")
    @JacksonXmlCData
    var returnCode: String = ""

    @JacksonXmlProperty(localName = "return_msg")
    @JacksonXmlCData
    var returnMsg: String = ""

    @JacksonXmlProperty(localName = "result_code")
    @JacksonXmlCData
    var resultCode:String = ""

    @JacksonXmlProperty(localName = "err_code")
    @JacksonXmlCData
    var errorCode:String = ""

    @JacksonXmlProperty(localName = "err_code_des")
    @JacksonXmlCData
    var errorCodeDesc:String = ""

    @JacksonXmlProperty(localName = "sign")
    @JacksonXmlCData
    var sign:String? = null
}