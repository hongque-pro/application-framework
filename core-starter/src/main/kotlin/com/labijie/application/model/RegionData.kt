package com.labijie.application.model

import com.labijie.application.data.domain.RegionArea
import com.labijie.application.data.domain.RegionCity
import com.labijie.application.data.domain.RegionProvince

/**
 *
 * @author lishiwen
 * @date 19-12-17
 * @since JDK1.8
 */
class RegionData(
    val provinces: List<RegionProvince>,
    val cities: List<RegionCity>,
    val areas: List<RegionArea>
)