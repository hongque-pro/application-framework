package com.labijie.application.mybatis

import org.mybatis.dynamic.sql.BasicColumn
import org.mybatis.dynamic.sql.render.TableAliasCalculator
import org.mybatis.dynamic.sql.update.UpdateDSL


fun <R, T> UpdateDSL<R>.SetClauseFinisher<T>.equalToColumnPlus(column: BasicColumn, value: Number) : UpdateDSL<R>{
    val columnName = column.renderWithTableAlias(TableAliasCalculator.empty())
    return this.equalToConstant("$columnName+$value")
}