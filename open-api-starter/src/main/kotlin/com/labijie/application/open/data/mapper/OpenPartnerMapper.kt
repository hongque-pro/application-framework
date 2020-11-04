/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import com.labijie.application.open.data.OpenPartnerRecord
import org.apache.ibatis.annotations.DeleteProvider
import org.apache.ibatis.annotations.InsertProvider
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.ResultMap
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.SelectProvider
import org.apache.ibatis.annotations.UpdateProvider
import org.apache.ibatis.type.JdbcType
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider
import org.mybatis.dynamic.sql.util.SqlProviderAdapter

@Mapper
interface OpenPartnerMapper {
    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    fun count(selectStatement: SelectStatementProvider): Long

    @DeleteProvider(type=SqlProviderAdapter::class, method="delete")
    fun delete(deleteStatement: DeleteStatementProvider): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insert")
    fun insert(insertStatement: InsertStatementProvider<OpenPartnerRecord>): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insertMultiple")
    fun insertMultiple(multipleInsertStatement: MultiRowInsertStatementProvider<OpenPartnerRecord>): Int

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @ResultMap("OpenPartnerRecordResult")
    fun selectOne(selectStatement: SelectStatementProvider): OpenPartnerRecord?

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @Results(id="OpenPartnerRecordResult", value = [
        Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        Result(column="time_expired", property="timeExpired", jdbcType=JdbcType.BIGINT),
        Result(column="status", property="status", jdbcType=JdbcType.TINYINT),
        Result(column="time_latest_paid", property="timeLatestPaid", jdbcType=JdbcType.BIGINT),
        Result(column="time_latest_updated", property="timeLatestUpdated", jdbcType=JdbcType.BIGINT),
        Result(column="phone_number", property="phoneNumber", jdbcType=JdbcType.VARCHAR),
        Result(column="contact", property="contact", jdbcType=JdbcType.VARCHAR),
        Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        Result(column="app_count", property="appCount", jdbcType=JdbcType.SMALLINT)
    ])
    fun selectMany(selectStatement: SelectStatementProvider): List<OpenPartnerRecord>

    @UpdateProvider(type=SqlProviderAdapter::class, method="update")
    fun update(updateStatement: UpdateStatementProvider): Int
}