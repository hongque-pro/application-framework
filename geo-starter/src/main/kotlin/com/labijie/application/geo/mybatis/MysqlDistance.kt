package com.labijie.application.geo.mybatis

import com.labijie.application.geo.ICoordinate
import com.labijie.infra.utils.ifNullOrBlank
import org.mybatis.dynamic.sql.BindableColumn
import org.mybatis.dynamic.sql.render.TableAliasCalculator
import java.sql.JDBCType

class MysqlDistance private constructor(
    private val coordinate: ICoordinate,
    private val column: BindableColumn<ByteArray>,
    alias:String = "distance",
    descending:Boolean = false) :
    ComputeSqlColumn<Double>(alias.ifNullOrBlank { "distance" }, JDBCType.DOUBLE, descending) {

    companion object {
        fun of(coordinate: ICoordinate, column: BindableColumn<ByteArray>, alias:String = "distance"): MysqlDistance {
            return MysqlDistance(coordinate, column, alias)
        }
    }

    override fun create(
        alias: String,
        jdbcType: JDBCType,
        descending: Boolean
    ): ComputeSqlColumn<Double> {
        return MysqlDistance(coordinate, column, alias, descending)
    }

    override fun renderWithTableAlias(tableAliasCalculator: TableAliasCalculator): String {
        val columnName = column.renderWithTableAlias(tableAliasCalculator)
        return ("st_distance_sphere(${columnName}, point(${coordinate.latitude}, ${coordinate.longitude}))")
    }
}