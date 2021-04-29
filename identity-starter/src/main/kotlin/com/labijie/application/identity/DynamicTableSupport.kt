package com.labijie.application.identity

import com.labijie.application.SpringContext
import com.labijie.application.identity.configuration.IdentityProperties
import org.mybatis.dynamic.sql.SqlTable
import org.springframework.beans.factory.getBean

object DynamicTableSupport {

    private var tablePrefix:String? = null

    var prefix: String
    get() {
        if(tablePrefix == null) {
            tablePrefix = if (SpringContext.isInitialized) {
                (SpringContext.current.getBeansOfType(IdentityProperties::class.java).map { it.value }.firstOrNull()
                    ?: IdentityProperties()).jdbcTablePrefix
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