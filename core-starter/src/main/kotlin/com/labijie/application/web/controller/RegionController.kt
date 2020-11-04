package com.labijie.application.web.controller

import com.labijie.application.web.annotation.ResponseWrapped
import com.labijie.application.web.data.domain.RegionArea
import com.labijie.application.web.data.domain.RegionCity
import com.labijie.application.web.data.domain.RegionProvince
import com.labijie.application.web.service.ICommonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author lishiwen
 * @date 19-12-17
 * @since JDK1.8
 */
@RestController
@ResponseWrapped
@RequestMapping("/commons")
class RegionController(
    private val commonService: ICommonService) {

    @GetMapping("/region-provinces")
    fun listRegionProvinces(): List<RegionProvince> {
        return commonService.listProvince()
    }

    @GetMapping("/region-cities")
    fun listRegionCities(@RequestParam(required = false) provinceId: String?): List<RegionCity> {
        return commonService.listCity(provinceId)
    }

    @GetMapping("/region-areas")
    fun listRegionAreas(@RequestParam(required = false) parentId: String?): List<RegionArea> {
        // parentId 可以是省id或者市id
        return commonService.listArea(parentId)
    }
}