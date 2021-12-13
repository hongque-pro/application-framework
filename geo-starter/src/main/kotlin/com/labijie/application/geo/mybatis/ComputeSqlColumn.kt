package com.labijie.application.geo.mybatis

import org.mybatis.dynamic.sql.BindableColumn
import org.mybatis.dynamic.sql.SortSpecification
import org.mybatis.dynamic.sql.render.TableAliasCalculator
import java.sql.JDBCType
import java.util.*

abstract class ComputeSqlColumn<T>(
    private var alias: String,
    private val jdbcType: JDBCType,
    private var descending: Boolean = false
) : BindableColumn<T>, SortSpecification {

    abstract override fun renderWithTableAlias(tableAliasCalculator: TableAliasCalculator): String

    private fun copy(): ComputeSqlColumn<T>{
        return this.create(this.alias, this.jdbcType, this.descending)
    }

    protected abstract fun create(alias:String, jdbcType:JDBCType, descending: Boolean):ComputeSqlColumn<T>

    override fun alias(): Optional<String> {
        return if(alias.isBlank()) Optional.empty() else Optional.ofNullable(alias)
    }

    override fun orderByName(): String? {
        return alias().orElse("")
    }

    override fun `as`(alias: String): BindableColumn<T> {
        val newThing = copy()
        newThing.alias = alias
        return newThing
    }

    override fun jdbcType(): Optional<JDBCType> {
        return Optional.of(JDBCType.DOUBLE)
    }

    override fun typeHandler(): Optional<String>? {
        return Optional.empty()
    }

    override fun descending(): SortSpecification {
        val newThing = copy()
        newThing.descending = true
        return newThing
    }

    override fun isDescending(): Boolean {
        return this.descending
    }

}