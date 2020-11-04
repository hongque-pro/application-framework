package com.labijie.application.huawei.model

class HuaweiMessageTemplates (var defaultTemplate: HuaweiMessageTemplateConfig? = null,
var templates: List<HuaweiMessageTemplateConfig>? = listOf()) {
    private var templatesMap:Map<String, HuaweiMessageTemplateConfig>;

    init {
        templatesMap = templates?.map { it.id to it }?.toMap() ?: mapOf()
    }

    fun getConfig(id: String): HuaweiMessageTemplateConfig? {
        return templatesMap[id] ?: defaultTemplate
    }
}