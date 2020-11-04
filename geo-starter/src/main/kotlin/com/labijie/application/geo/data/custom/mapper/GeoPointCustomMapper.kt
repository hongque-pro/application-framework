package com.labijie.application.geo.data.custom.mapper

import com.labijie.application.geo.data.custom.GeoPointWithDistanceRecord
import com.labijie.application.geo.data.mapper.GeoPointMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.ResultMap
import org.apache.ibatis.annotations.SelectProvider
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider
import org.mybatis.dynamic.sql.util.SqlProviderAdapter

@Mapper
interface GeoPointCustomMapper : GeoPointMapper {

    @SelectProvider(type= SqlProviderAdapter::class, method="select")
    @ResultMap("GeoPointRecordResult")
    fun selectManyWithDistance(selectStatement: SelectStatementProvider): List<GeoPointWithDistanceRecord>
}