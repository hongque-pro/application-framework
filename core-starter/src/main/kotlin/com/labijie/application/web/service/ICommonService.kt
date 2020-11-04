package com.labijie.application.web.service

import com.labijie.application.web.data.domain.RegionArea
import com.labijie.application.web.data.domain.RegionCity
import com.labijie.application.web.data.domain.RegionProvince


/**
 *
 * @author lishiwen
 * @date 19-9-9
 * @since JDK1.8
 */
interface ICommonService {

    fun listProvince(): List<RegionProvince>

    // provinceId为null时获取全部
    fun listCity(provinceId: String? = null): List<RegionCity>

    // parentId可以是cityId或者provinceId，为null时获取全部
    fun listArea(parentId: String? = null): List<RegionArea>
}