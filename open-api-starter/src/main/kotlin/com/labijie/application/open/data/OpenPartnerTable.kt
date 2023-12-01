package com.labijie.application.open.data

import com.labijie.application.open.model.OpenPartnerStatus
import com.labijie.infra.orm.SimpleLongIdTable

/**
 * @author Anders Xiao
 * @date 2023-11-30
 */
object OpenPartnerTable : SimpleLongIdTable("open_partners") {
    val name = varchar("name", 32)
    val timeExpired = long("time_expired").index()
    val status = enumeration("status", OpenPartnerStatus::class)
    val timeLatestPaid = long("time_latest_paid").default(0)
    val timeLatestUpdated = long("time_latest_updated").default(0)
    val phoneNumber = varchar("phone_number", 18)
    val contact = varchar("contact", 16)
    val email = varchar("email", 64)
    val appCount = integer("appCount")
}