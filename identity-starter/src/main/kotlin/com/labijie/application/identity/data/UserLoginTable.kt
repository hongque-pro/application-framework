package com.labijie.application.identity.data

import com.labijie.infra.orm.compile.KspPrimaryKey

/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
object UserLoginTable : IdentityTable("user_logins") {

    @KspPrimaryKey
    val loginProvider = varchar("login_provider", 16)
    @KspPrimaryKey
    val providerKey = varchar("provider_key", 128)
    val providerDisplayName = varchar("provider_display_name", 32)
    val userId = long("user_id").index()

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(loginProvider, providerKey)
}