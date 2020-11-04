package com.labijie.application.geo

import com.labijie.application.ForwardList

interface IGeoService {
    /**
     * 获得指定中心点半径范围内的点
     */
    fun getPointsInDistance(
        center: ICoordinate,
        distanceMeters: Double,
        pointType: Int? = null
    ): List<MapPointWithDistance>

    /**
     * 分页方式获得指定中心点半径范围内的点（按距离排序）
     */
    fun getPointsInDistancePage(
        center: ICoordinate,
        distanceMeters: Double,
        pageSize: Int,
        forwardToken: String? = null,
        pointType: Int? = null
    ): ForwardList<MapPointWithDistance>

    fun addPoint(mapPoint: MapPoint): Boolean

    fun deletePoint(id: Long): Boolean

    fun updatePoint(id: Long, coordinate: ICoordinate?, name:String?):Boolean

    fun getPoint(id:Long):MapPoint?
}