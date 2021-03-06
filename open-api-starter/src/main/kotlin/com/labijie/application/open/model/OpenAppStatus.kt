package com.labijie.application.open.model

import com.labijie.application.IDescribeEnum

enum class OpenAppStatus(override val code: Byte, override val description: String) : IDescribeEnum<Byte> {
    DISABLED(0, "禁用"),
    NORMAL(1,  "正常"),
}