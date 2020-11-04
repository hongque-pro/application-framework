package com.labijie.application.open.model

data class OpenPartnerEntry(
    var id: Long = 0,
    var name: String = "",
    var timeExpired: Long = System.currentTimeMillis(),
    var status: OpenPartnerStatus = OpenPartnerStatus.NORMAL
)