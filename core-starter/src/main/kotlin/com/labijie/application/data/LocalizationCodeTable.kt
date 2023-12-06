package com.labijie.application.data

import com.labijie.infra.orm.compile.KspPrimaryKey
import org.jetbrains.exposed.sql.Table

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
object LocalizationCodeTable : Table("localization_codes") {
    @KspPrimaryKey
    val code = varchar("code", 64)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(code)
}