package com.labijie.application.huawei.model

class HuaweiMessageTemplateConfig(
    var id: String = "",
    var sender: String = "",
    var signature: String? = null,
    var params: List<String> = listOf()
)