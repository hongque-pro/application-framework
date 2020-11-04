package com.labijie.application.auth

import com.labijie.application.SpringContext
import com.labijie.application.auth.configuration.AuthServerProperties
import org.mybatis.dynamic.sql.SqlTable
import org.springframework.beans.factory.getBean

object AuthDynamicTableSupport {

    private var tablePrefix:String? = null

    var prefix: String
    get() {
        if(tablePrefix == null) {
            tablePrefix = if (SpringContext.isInitialized) {
                (SpringContext.current.getBeansOfType(AuthServerProperties::class.java).map { it.value }.firstOrNull()
                    ?: AuthServerProperties()).jdbcTablePrefix
            } else {
                 ""
            }
        }
        return tablePrefix.orEmpty()
    }
    set(value) {
        this.tablePrefix = value
    }

    fun getTable(baseTable: SqlTable) = SqlTable.of("$prefix${baseTable.tableNameAtRuntime().removePrefix("identity_")}")

}