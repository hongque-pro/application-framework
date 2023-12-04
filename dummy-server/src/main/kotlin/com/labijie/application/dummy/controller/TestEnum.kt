package com.labijie.application.dummy.controller

import com.labijie.application.IDescribeEnum

/**
 * @author Anders Xiao
 * @date 2023-12-04
 */
enum class TestEnum(override val code: Byte, override val description: String) : IDescribeEnum<Byte> {
    Ten(10, "Ten"),
    Twenty(20, "Twenty")
}