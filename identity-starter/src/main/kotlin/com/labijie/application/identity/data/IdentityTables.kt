package com.labijie.application.identity.data

import com.labijie.infra.orm.SimpleLongIdTable
import org.jetbrains.exposed.sql.Table


/**
 * @author Anders Xiao
 * @date 2023-11-29
 */

abstract class IdentityTable(tableName: String) : Table(tableName) {

    val tablePrefix = "identity_"
    override val tableName: String
        get() = "${tablePrefix}${super.tableName}"
}

abstract class IdentityLongIdTable(tableName: String) : SimpleLongIdTable(tableName) {
    val tablePrefix = "identity_"
    override val tableName: String
        get() = "${tablePrefix}${super.tableName}"
}

