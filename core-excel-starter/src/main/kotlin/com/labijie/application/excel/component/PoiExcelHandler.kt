package com.labijie.application.excel.component

import com.labijie.application.excel.model.ExcelSheetItem
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream
import java.text.DecimalFormat

/**
 *
 * @author lishiwen
 * @date 19-9-26
 * @since JDK1.8
 */
@Component
class PoiExcelHandler : IExcelHandler {

    override fun readXssf(inputStream: InputStream): List<ExcelSheetItem> {
        return readWorkbook(XSSFWorkbook(inputStream))
    }

    override fun readXssfWithHeader(inputStream: InputStream): List<ExcelSheetItem> {
        return mergeHeader(readXssf(inputStream))
    }

    override fun readXssf(filePath: String): List<ExcelSheetItem> {
        return readWorkbook(XSSFWorkbook(File(filePath)))
    }

    override fun readXssfWithHeader(filePath: String): List<ExcelSheetItem> {
        return mergeHeader(readXssf(filePath))
    }

    override fun writeXssf(filePath: String, sheets: List<ExcelSheetItem>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun mergeHeader(data: List<ExcelSheetItem>): List<ExcelSheetItem> {
        return data.map { item ->
            ExcelSheetItem(
                name = item.name,
                rows = when (item.rows.size) {
                    0 -> listOf()
                    1 -> listOf()
                    else -> {
                        val header = item.rows[0]
                        item.rows.subList(1, item.rows.size).map { row ->
                            val rowData = mutableMapOf<String, Any?>()
                            if (row.isNotEmpty()) {
                                header.forEach { t, u ->
                                    rowData[u.toString()] = row.getOrDefault(t, null)
                                }
                            }
                            rowData
                        }.filter { r -> r.isNotEmpty() }
                    }
                }
            )
        }
    }

    private fun readWorkbook(workbook: XSSFWorkbook): List<ExcelSheetItem> {
        return workbook.sheetIterator().asSequence().map { sheet ->
            ExcelSheetItem(
                name = sheet.sheetName,
                rows = sheet.rowIterator().asSequence().map { r ->
                    r.cellIterator().asSequence().map { c ->

                        val value = when (c.cellType) {
                            CellType.STRING -> c.stringCellValue as Any
                            CellType.NUMERIC ->
                                if (DateUtil.isCellDateFormatted(c)) c.dateCellValue
                                else DecimalFormat("#").format(c.numericCellValue)
                            CellType.BOOLEAN -> c.booleanCellValue
                            else -> ""
                        }
                        Pair(c.columnIndex.toString(), value)
                    }.toMap()
                }.toList()
            )
        }.toList()
    }
}