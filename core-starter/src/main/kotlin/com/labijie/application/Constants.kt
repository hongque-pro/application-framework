package com.labijie.application

import com.labijie.infra.utils.toEpochMilli
import java.time.LocalDateTime

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-12
 */
object Constants {
    const val EN_US = "en-US"
    const val ZH_CN = "zh-CN"
    val MinTimeMillis:Long = LocalDateTime.of(1970, 1, 1, 0, 0).toEpochMilli()
    val MaxTimeMillis:Long = LocalDateTime.of(2999, 12, 31, 23, 59, 59).toEpochMilli()
}