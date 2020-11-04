package com.labijie.application.open.model

data class OpenPartner(
    var id: Long = 0,
    var name: String = "",
    var appCount: Short = 0,
    var timeExpired: Long = System.currentTimeMillis(),
    var status: OpenPartnerStatus = OpenPartnerStatus.NORMAL,
    var timeLatestPaid: Long = 0,
    var timeLatestUpdated: Long = System.currentTimeMillis(),
    var phoneNumber: String = "",
    var contact: String = "",
    var email: String = ""
)