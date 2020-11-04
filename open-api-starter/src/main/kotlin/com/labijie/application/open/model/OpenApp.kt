package com.labijie.application.open.model

import com.labijie.infra.utils.ShortId
import java.util.*

data class OpenApp(
    var appId: Long = 0,
    val signAlgorithm: String = "",
    var displayName: String = "",
    var jsApiDomain: String = "",
    var logoUrl: String = "",
    var status: OpenAppStatus = OpenAppStatus.NORMAL,
    var partnerId: Long = 0,
    var timeCreated: Long = System.currentTimeMillis(),
    var timeConfigUpdated: Long = System.currentTimeMillis(),
    var concurrencyStamp: String = ShortId.newId(),
    var config: Map<String, String> = mapOf()
)