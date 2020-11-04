package com.labijie.application.huawei.model

class HuaweiSmsResponse (
    var code: String = "E000000",
    var description: String = "未知异常",
    var result: List<HuaweiSmsResponseItem>? = null
)


class HuaweiSmsResponseItem(
    var originTo: String = "",
    var createTime: String = "",
    var from: String = "",
    var smsMsgId: String = "",
    var status: String = ""
)