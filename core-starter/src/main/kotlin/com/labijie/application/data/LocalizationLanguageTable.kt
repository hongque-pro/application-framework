package com.labijie.application.data

import com.labijie.infra.orm.compile.KspPrimaryKey
import org.jetbrains.exposed.sql.Table

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
object LocalizationLanguageTable: Table("localization_languages") {
    @KspPrimaryKey
    val locale = varchar("locale", 8)
    //ISO 639 is three letter
    val language = varchar("language", 4).index()
    val country = varchar("region", 4)
    val disabled = bool("disabled").default(false)
    val default = bool("default").default(false)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(locale)
}