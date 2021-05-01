/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.data.mapper

import com.labijie.application.data.SnowflakeSlotRecord
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
interface SnowflakeSlotMapper {
    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    fun count(selectStatement: SelectStatementProvider): Long

    @DeleteProvider(type=SqlProviderAdapter::class, method="delete")
    fun delete(deleteStatement: DeleteStatementProvider): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insert")
    fun insert(insertStatement: InsertStatementProvider<SnowflakeSlotRecord>): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insertMultiple")
    fun insertMultiple(multipleInsertStatement: MultiRowInsertStatementProvider<SnowflakeSlotRecord>): Int

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @ResultMap("SnowflakeSlotRecordResult")
    fun selectOne(selectStatement: SelectStatementProvider): SnowflakeSlotRecord?

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @Results(id="SnowflakeSlotRecordResult", value = [
        Result(column="slot_number", property="slotNumber", jdbcType=JdbcType.SMALLINT, id=true),
        Result(column="instance", property="instance", jdbcType=JdbcType.VARCHAR),
        Result(column="addr", property="addr", jdbcType=JdbcType.VARCHAR),
        Result(column="time_expired", property="timeExpired", jdbcType=JdbcType.BIGINT)
    ])
    fun selectMany(selectStatement: SelectStatementProvider): List<SnowflakeSlotRecord>

    @UpdateProvider(type=SqlProviderAdapter::class, method="update")
    fun update(updateStatement: UpdateStatementProvider): Int
}