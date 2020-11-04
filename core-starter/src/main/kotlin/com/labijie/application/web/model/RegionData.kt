package com.labijie.application.web.model

import com.labijie.application.web.data.domain.RegionArea
import com.labijie.application.web.data.domain.RegionCity
import com.labijie.application.web.data.domain.RegionProvince

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