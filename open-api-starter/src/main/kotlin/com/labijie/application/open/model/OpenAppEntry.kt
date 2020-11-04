package com.labijie.application.open.model

data class OpenAppEntry (
    var appId: Long = 0,
    var displayName: String = "",
    var logoUrl: String = "",
    var appCount: Short = 0,
    var status: OpenAppStatus = OpenAppStatus.NORMAL,
    var timeCreated: Long = System.currentTimeMillis()
)