package com.labijie.application.geo.impl

import com.labijie.application.ForwardList
import com.labijie.application.geo.*
import com.labijie.application.geo.configuration.GeoProperties
import com.labijie.application.geo.data.GeoPointRecord
import com.labijie.application.geo.data.custom.mapper.GeoPointCustomMapper
import com.labijie.application.geo.data.custom.GeoPointWithDistanceRecord
import com.labijie.application.geo.data.mapper.*
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint
import com.labijie.application.geo.mybatis.MysqlDistance
import com.uber.h3core.H3Core
import com.uber.h3core.util.GeoCoord
import org.mybatis.dynamic.sql.SqlBuilder.*
import org.mybatis.dynamic.sql.render.RenderingStrategies
import org.mybatis.dynamic.sql.select.QueryExpressionDSL
import org.mybatis.dynamic.sql.select.SelectModel
import org.slf4j.LoggerFactory
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.util.Base64Utils
import java.lang.StringBuilder
import java.sql.JDBCType

class H3GeoService(
    private val transactionTemplate: TransactionTemplate,
    private val geoPointMapper: GeoPointCustomMapper,
    private val geoProperties: GeoProperties
) : IGeoService {

    private val resolutions = H3Resolutions.values().apply {
        sortBy { r -> r.edgeMeters }
    }

    companion object {

        private val distanceColumnName = GeoPointWithDistanceRecord::distance.name

        private val logger = LoggerFactory.getLogger(H3GeoService::class.java)

        private val h3Core: H3Core by lazy {
            H3Core.newInstance()
        }
    }


    private fun selectByDistance(
        center: ICoordinate,
        distanceLimitMeters: Double,
        pointType: Int?,
        countLimit: Long? = null,
        customFilter: ((where: QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder, distanceColumn: MysqlDistance) -> Unit)? = null
    ): List<GeoPointWithDistanceRecord> {

        val funColumn = MysqlDistance.of(center, GeoPoint.gpsLatLng).`as`(distanceColumnName) as MysqlDistance

        val selectProvider = select(
            GeoPoint.id,
            GeoPoint.pointName,
            GeoPoint.pointType,
            GeoPoint.mapLatLng,
            funColumn
        )
            .from(GeoPoint)
            .where()
            .applyCell(distanceLimitMeters, center)
            .let {
                if (pointType != null) {
                    it.and(GeoPoint.pointType, isEqualTo(pointType))
                }
                customFilter?.invoke(it, funColumn)
                it.and(funColumn, isLessThanOrEqualTo(distanceLimitMeters))
            }
            .orderBy(funColumn, GeoPoint.id).let {
                if (countLimit != null && countLimit > 0) {
                    it.limit(countLimit)
                } else {
                    it
                }
            }
            .build()
            .render(RenderingStrategies.MYBATIS3)

        return geoPointMapper.selectManyWithDistance(selectProvider)
    }

    override fun getPointsInDistance(
        center: ICoordinate,
        distanceMeters: Double,
        pointType: Int?
    ): List<MapPointWithDistance> {
        val list = selectByDistance(center, distanceMeters, pointType)
        return list.map {
            val c = GeoMysqlUtils.fromMysqlPoint(it.mapLatLng!!)
            MapPointWithDistance(it.id, it.pointName, it.pointType, c.latitude, c.longitude, it.distance)
        }
    }

    override fun getPointsInDistancePage(
        center: ICoordinate,
        distanceMeters: Double,
        pageSize: Int,
        forwardToken: String?,
        pointType: Int?
    ): ForwardList<MapPointWithDistance> {
        if (pageSize <= 0) {
            return ForwardList()
        }

        val list = selectByDistance(center, distanceMeters, pointType, pageSize.toLong()) { builder, col ->
            builder.applyDistanceForwardToken(col, forwardToken)
        }
        val data = list.map {
            val c = GeoMysqlUtils.fromMysqlPoint(it.mapLatLng!!)
            MapPointWithDistance(it.id, it.pointName, it.pointType, c.latitude, c.longitude, it.distance)
        }
        //编码游标
        val nextToken = encodeDistanceForwardToken(list, pageSize)
        return ForwardList(data, nextToken)
    }

    override fun addPoint(mapPoint: MapPoint): Boolean {
        val record = GeoPointRecord().apply {
            this.id = mapPoint.id
            this.mapLatLng = GeoMysqlUtils.toMysqlPoint(mapPoint)
            this.pointType = mapPoint.pointType
            this.pointName = mapPoint.pointName
            this.setGPSCoordinate(mapPoint)
        }

        val count = transactionTemplate.execute {
            geoPointMapper.insert(record)
        }
        return count != null && count > 0
    }

    private fun GeoPointRecord.setGPSCoordinate(mapCoordinate: ICoordinate) {
        val gpsCoordinate = getGPSCoordinate(mapCoordinate.latitude, mapCoordinate.longitude)
        this.gpsLatLng = GeoMysqlUtils.toMysqlPoint(gpsCoordinate)
        this.h3CellRe3 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R3.value)
        this.h3CellRe4 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R4.value)
        this.h3CellRe5 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R5.value)
        this.h3CellRe6 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R6.value)
        this.h3CellRe7 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R7.value)
        this.h3CellRe8 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R8.value)
        this.h3CellRe9 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R9.value)
        this.h3CellRe10 = h3Core.geoToH3(gpsCoordinate.latitude, gpsCoordinate.longitude, H3Resolutions.R10.value)

        if(logger.isDebugEnabled){
           StringBuilder().let {
                it.appendLine("appy uber h3 algorithm for coordinate ( lat: ${mapCoordinate.latitude}, lng:${mapCoordinate.longitude} )")
                it.appendLine("get gps coordinate ( lat: ${gpsCoordinate.latitude}, lng:${gpsCoordinate.longitude} )")
                it.appendLine("res-3 cell: ${this.h3CellRe3}")
               it.appendLine("res-4 cell: ${this.h3CellRe4}")
               it.appendLine("res-5 cell: ${this.h3CellRe5}")
               it.appendLine("res-6 cell: ${this.h3CellRe6}")
               it.appendLine("res-7 cell: ${this.h3CellRe7}")
               it.appendLine("res-8 cell: ${this.h3CellRe8}")
               it.appendLine("res-9 cell: ${this.h3CellRe9}")
               it.appendLine("res-10 cell: ${this.h3CellRe10}")

               logger.debug(it.toString())
            }
        }
    }

    override fun deletePoint(id: Long): Boolean {
        val count = this.transactionTemplate.execute {
            geoPointMapper.deleteByPrimaryKey(id)
        }
        return (count ?: 0) > 0
    }

    override fun updatePoint(id: Long, coordinate: ICoordinate?, name: String?): Boolean {
        if(coordinate == null && name == null){
            return false
        }
        val record = GeoPointRecord().apply {
            this.id = id
            this.mapLatLng = if(coordinate != null) GeoMysqlUtils.toMysqlPoint(coordinate) else null
            this.pointName = name

            if(coordinate != null){
                this.setGPSCoordinate(coordinate)
            }
        }
        val count = this.transactionTemplate.execute {
            geoPointMapper.updateByPrimaryKeySelective(record)
        }
        return (count ?: 0) > 0
    }

    override fun getPoint(id: Long): MapPoint? {
        val selectProvider = select(
            GeoPoint.id,
            GeoPoint.pointName,
            GeoPoint.pointType,
            GeoPoint.mapLatLng
        )
            .from(GeoPoint)
            .where(GeoPoint.id, isEqualTo(id))
            .build()
            .render(RenderingStrategies.MYBATIS3)

        val record = geoPointMapper.selectOne(selectProvider)
        return if(record != null){
            val coordinate = GeoMysqlUtils.fromMysqlPoint(record.mapLatLng!!)
            MapPoint(record.id!!, record.pointName!!, record.pointType!!, coordinate.latitude, coordinate.longitude)
        }
        else null
    }

    /**
     * 应用距离查询中的分页游标，游标格式 "id:distance"
     */
    private fun QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder.applyDistanceForwardToken(
        distanceColumn: MysqlDistance,
        forwardToken: String?
    ) {
        val splters = forwardToken?.let {
            val tokenString = Base64Utils.decodeFromUrlSafeString(it).toString(Charsets.UTF_8)
            tokenString.split(":")
        }
        if (splters != null && splters.size == 2) {
            val id = splters[0].toLong()
            val distance = splters[1].toDouble()
            this.and(distanceColumn, isGreaterThanOrEqualTo(distance))
                .and(GeoPoint.id, isGreaterThan(id))
        }
    }

    /**
     * 解析游标，游标格式 "id:distance"
     */
    private fun encodeDistanceForwardToken(
        list: List<GeoPointWithDistanceRecord>,
        pageSize: Int
    ): String? {
        return if (list.size == pageSize) {
            val last = list.last()
            val tokenString = "${last.id}:${last.distance}"
            Base64Utils.encodeToUrlSafeString(tokenString.toByteArray(Charsets.UTF_8))
        } else {
            null
        }
    }

    private fun QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder.applyCell(
        radiusMeters: Double,
        center: ICoordinate
    ): QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder {
        val res = determineResolutionsByRadius(radiusMeters)
        val cells = findBoundaryCells(center, res)
        when (res) {
            H3Resolutions.R3 -> this.and(GeoPoint.h3CellRe3, isIn(cells))
            H3Resolutions.R4 -> this.and(GeoPoint.h3CellRe4, isIn(cells))
            H3Resolutions.R5 -> this.and(GeoPoint.h3CellRe5, isIn(cells))
            H3Resolutions.R6 -> this.and(GeoPoint.h3CellRe6, isIn(cells))
            H3Resolutions.R7 -> this.and(GeoPoint.h3CellRe7, isIn(cells))
            H3Resolutions.R8 -> this.and(GeoPoint.h3CellRe8, isIn(cells))
            H3Resolutions.R9 -> this.and(GeoPoint.h3CellRe9, isIn(cells))
            H3Resolutions.R10 -> this.and(GeoPoint.h3CellRe10, isIn(cells))
        }
        return this
    }

    private fun findBoundaryCells(center: ICoordinate, res: H3Resolutions): List<Long> {
        val point = getGPSCoordinate(center.latitude, center.longitude)
        //得到中心
        val cellId = h3Core.geoToH3(point.latitude, point.longitude, res.value)
        val cells = h3Core.h3ToGeoBoundary(cellId).apply {
            this.add(GeoCoord(point.latitude, point.longitude))
        }
        return cells.map {
            h3Core.geoToH3(it.lat, it.lng, res.value)
        }.distinct()
    }

    /**
     * 根据半径确定使用的单元格精度
     */
    private fun determineResolutionsByRadius(radiusMeters: Double): H3Resolutions {
        val length = ((radiusMeters * 2) / 4).toLong() //临近 6 个单元格使用4条边的长度
        return resolutions.firstOrNull {
            length <= it.edgeMeters
        } ?: H3Resolutions.R3
    }

    private fun getGPSCoordinate(lat: Double, lon: Double): ICoordinate {
        if (this.geoProperties.coordinateSystem == CoordinateSystem.GCJ02) {
            val c = CoordinateUtils.gcj02_To_Gps84(lat, lon)
            return Coordinate(c[0], c[1])
        }
        return Coordinate(lat, lon)
    }

    private fun getMapCoordinate(lat: Double, lon: Double): ICoordinate {
        if (this.geoProperties.coordinateSystem == CoordinateSystem.GCJ02) {
            val c = CoordinateUtils.gps84_To_Gcj02(lat, lon)
            return Coordinate(c[0], c[1])
        }
        return Coordinate(lat, lon)
    }
}