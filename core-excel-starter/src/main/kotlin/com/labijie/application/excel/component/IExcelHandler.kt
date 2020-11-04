package com.labijie.application.excel.component

import com.labijie.application.excel.model.ExcelSheetItem
import java.io.InputStream

/**
 *
 * @author lishiwen
 * @date 19-9-26
 * @since JDK1.8
 */
interface IExcelHandler {

    fun readXssf(filePath: String): List<ExcelSheetItem>

    fun readXssfWithHeader(filePath: String): List<ExcelSheetItem>

    fun readXssf(inputStream: InputStream): List<ExcelSheetItem>

    fun readXssfWithHeader(inputStream: InputStream): List<ExcelSheetItem>

    fun writeXssf(filePath: String, sheets: List<ExcelSheetItem>)
}