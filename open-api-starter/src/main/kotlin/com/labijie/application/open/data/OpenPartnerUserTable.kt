package com.labijie.application.open.data

import org.jetbrains.exposed.sql.Table


/**
 * @author Anders Xiao
 * @date 2023-11-30
 */
object OpenPartnerUserTable : Table("open_partner_users") {
    val userId = long("user_id").index()
    val partnerId = long("partner_id")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId, partnerId)
}