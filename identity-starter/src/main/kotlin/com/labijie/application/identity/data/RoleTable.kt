package com.labijie.application.identity.data

/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
object RoleTable : IdentityLongIdTable("roles") {
    val concurrencyStamp = varchar("concurrency_stamp", 32)
    val name = varchar("name", 16).uniqueIndex()
}