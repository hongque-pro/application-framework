package com.labijie.application.identity.data

import com.labijie.infra.orm.compile.KspPrimaryKey

/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
object UserRoleTable : IdentityTable("user_roles") {
    @KspPrimaryKey
    val userId = long("user_id").index()
    @KspPrimaryKey
    val roleId = long("role_id").index()

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId, roleId)
}