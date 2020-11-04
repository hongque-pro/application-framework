package com.labijie.application.geo

/**
 * H3 单元格精度（参考：https://h3geo.org/docs/core-library/restable）
 */
enum class H3Resolutions(val value:Int, val squareMeters : Long,  val edgeMeters:Long) {
    /**
     * 六边形面积:1200 KM2, 边长：60KM
     */
    R3(3, 12392264862, 59810),
    /**
     * 六边形面积:1770 KM2, 边长：23KM
     */
    R4(4, 1770323551, 22606),
    /**
     * 六边形面积:253 KM2, 边长：8.5KM
     */
    R5(5, 252903364, 8544),
    /**
     * 六边形面积:36 KM2, 边长：3KM
     */
    R6(6, 36129052, 3229),
    /**
     * 六边形面积:5 KM2, 边长：1 KM
     */
    R7(7, 5161293, 1220),
    /**
     * 六边形面积: 0.74 KM2, 边长：461 M
     */
    R8(8, 737327, 461),
    /**
     * 六边形面积: 0.1 KM2, 边长：174 M
     */
    R9(9, 105332, 174),
    /**
     * 六边形面积: 15047 M2, 边长：66 M
     */
    R10(10, 15047, 66),
}