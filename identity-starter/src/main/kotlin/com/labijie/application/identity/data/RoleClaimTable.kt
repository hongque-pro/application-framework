package com.labijie.application.identity.data

/**
 * @author Anders Xiao
 * @date 2023-11-29
 */
object RoleClaimTable : IdentityLongIdTable("role_claims") {
    val claimType = varchar("claim_type", 16)
    val claimValue = varchar("claim_value", 256)
    val roleId = long("role_id").index()

}