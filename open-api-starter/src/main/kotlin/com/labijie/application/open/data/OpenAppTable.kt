package com.labijie.application.open.data

import com.labijie.application.open.model.OpenAppStatus
import com.labijie.infra.orm.SimpleLongIdTable

/**
 * @author Anders Xiao
 * @date 2023-11-30
 */
object OpenAppTable : SimpleLongIdTable("open_apps", "app_id") {
    val displayName = varchar("display_name", 64)
    val appSecret = varchar("app_secret", 128).index()
    val appType = byte("app_type")
    val signAlgorithm = varchar("sign_algorithm", 8)
    val jsApiKey = varchar("js_api_key", 256).index()
    val jsApiDomain = varchar("js_api_domain", 64)
    val logoUrl = varchar("logo_url", 256)
    val status = enumeration("status", OpenAppStatus::class)
    val partnerId = long("partner_id").index()
    val timeCreated = long("time_created").index()
    val timeConfigUpdated = long("time_config_updated")
    val concurrencyStamp = varchar("concurrency_stamp", 32)
    val configuration = varchar("configuration", 4096).default("{}")
}