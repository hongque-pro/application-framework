/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
package com.labijie.application.data

import com.labijie.infra.orm.compile.KspPrimaryKey
import org.jetbrains.exposed.sql.Table

object LocalizationMessageTable : Table("localization_messages") {
    @KspPrimaryKey
    var locale = varchar("locale", 8)
    @KspPrimaryKey
    var code = varchar("code",128)

    var message = mediumText("message")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(locale, code)
}