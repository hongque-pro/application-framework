package com.labijie.application.service.impl

import com.labijie.application.model.CoreCacheKeys
import com.labijie.application.data.domain.*
import com.labijie.application.data.mapper.RegionAreaMapper
import com.labijie.application.data.mapper.RegionCityMapper
import com.labijie.application.data.mapper.RegionProvinceMapper
import com.labijie.application.model.RegionData
import com.labijie.application.service.ICommonService
import com.labijie.caching.ICacheManager
import com.labijie.caching.getOrSet
import org.springframework.stereotype.Service
import java.time.Duration

/**
 *
 * @author lishiwen
 * @date 19-12-17
 * @since JDK1.8
 */
@Service
class CommonsService(
    private val cacheManager: ICacheManager,
    private val provinceMapper: RegionProvinceMapper,
    private val cityMapper: RegionCityMapper,
    private val areaMapper: RegionAreaMapper) : ICommonService {

    override fun listProvince(): List<RegionProvince> {
        return getRegionData().provinces
    }

    override fun listCity(provinceId: String?): List<RegionCity> {
        return getRegionData().cities.let {
            if (provinceId == null) it
            else it.filter { c -> c.id.startsWith(provinceId) }
        }
    }

    override fun listArea(parentId: String?): List<RegionArea> {
        return getRegionData().areas.let {
            if (parentId == null) it
            else it.filter { a -> a.id.startsWith(parentId) }
        }
    }

    private fun getRegionData(): RegionData {
        return cacheManager.getOrSet(CoreCacheKeys.REGION_CACHE_DATA, "REGION_DATA", Duration.ofHours(1)) {
            RegionData(
                provinces = provinceMapper.selectByExample(RegionProvinceExample().apply {
                    this.orderByClause = RegionProvince.Column.id.asc()
                }),
                cities = cityMapper.selectByExample(RegionCityExample().apply {
                    this.orderByClause = RegionCity.Column.id.asc()
                }),
                areas = areaMapper.selectByExample(RegionAreaExample().apply {
                    this.orderByClause = RegionArea.Column.id.asc()
                })
            )
        }!!
    }
}