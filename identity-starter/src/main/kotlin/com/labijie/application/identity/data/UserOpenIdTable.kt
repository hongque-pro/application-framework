package com.labijie.application.identity.data

import com.labijie.infra.orm.compile.KspPrimaryKey
import org.jetbrains.exposed.sql.Table

/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
object UserOpenIdTable : IdentityTable("user_openids") {
    @KspPrimaryKey
    val userId = long("user_id").index()
    @KspPrimaryKey
    val appId = varchar("app_id", 32).index()
    @KspPrimaryKey
    val loginProvider = varchar("login_provider", 16).index()
    val openId = varchar("open_id", 128)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId, appId, loginProvider)
}