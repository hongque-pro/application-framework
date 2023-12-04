package com.labijie.application.model

import com.labijie.application.IDescribeEnum

enum class OrderStatus(override val code: Byte, override val description: String): IDescribeEnum<Byte> {
    OPENED(10, "正常"),
    CLOSED(100, "已关闭")
}