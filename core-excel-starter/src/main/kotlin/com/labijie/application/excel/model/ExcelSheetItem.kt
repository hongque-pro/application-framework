package com.labijie.application.excel.model

/**
 *
 * @author lishiwen
 * @date 19-9-26
 * @since JDK1.8
 */
data class ExcelSheetItem(
    val name: String,
    val rows: List<Map<String, Any?>>
)
