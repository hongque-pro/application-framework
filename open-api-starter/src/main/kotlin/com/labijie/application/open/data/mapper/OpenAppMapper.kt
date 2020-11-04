/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import com.labijie.application.open.data.OpenAppRecord
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
interface OpenAppMapper {
    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    fun count(selectStatement: SelectStatementProvider): Long

    @DeleteProvider(type=SqlProviderAdapter::class, method="delete")
    fun delete(deleteStatement: DeleteStatementProvider): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insert")
    fun insert(insertStatement: InsertStatementProvider<OpenAppRecord>): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insertMultiple")
    fun insertMultiple(multipleInsertStatement: MultiRowInsertStatementProvider<OpenAppRecord>): Int

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @ResultMap("OpenAppRecordResult")
    fun selectOne(selectStatement: SelectStatementProvider): OpenAppRecord?

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @Results(id="OpenAppRecordResult", value = [
        Result(column="app_id", property="appId", jdbcType=JdbcType.BIGINT, id=true),
        Result(column="display_name", property="displayName", jdbcType=JdbcType.VARCHAR),
        Result(column="app_secret", property="appSecret", jdbcType=JdbcType.CHAR),
        Result(column="app_type", property="appType", jdbcType=JdbcType.TINYINT),
        Result(column="sign_algorithm", property="signAlgorithm", jdbcType=JdbcType.VARCHAR),
        Result(column="js_api_key", property="jsApiKey", jdbcType=JdbcType.CHAR),
        Result(column="js_api_domain", property="jsApiDomain", jdbcType=JdbcType.VARCHAR),
        Result(column="logo_url", property="logoUrl", jdbcType=JdbcType.VARCHAR),
        Result(column="status", property="status", jdbcType=JdbcType.TINYINT),
        Result(column="partner_id", property="partnerId", jdbcType=JdbcType.BIGINT),
        Result(column="time_created", property="timeCreated", jdbcType=JdbcType.BIGINT),
        Result(column="time_config_updated", property="timeConfigUpdated", jdbcType=JdbcType.BIGINT),
        Result(column="concurrency_stamp", property="concurrencyStamp", jdbcType=JdbcType.VARCHAR),
        Result(column="configuration", property="configuration", jdbcType=JdbcType.LONGVARCHAR)
    ])
    fun selectMany(selectStatement: SelectStatementProvider): List<OpenAppRecord>

    @UpdateProvider(type=SqlProviderAdapter::class, method="update")
    fun update(updateStatement: UpdateStatementProvider): Int
}