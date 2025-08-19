package com.labijie.application.data

import com.labijie.application.model.FileModifier
import com.labijie.infra.orm.SimpleLongIdTable

/**
 * @author Anders Xiao
 * @date 2023-12-05
 */
object FileIndexTable : SimpleLongIdTable("file_indices") {
    val path = varchar("path", 256).uniqueIndex()
    val timeCreated = long("time_created").index()
    val fileType = varchar("file_type", 32)
    val sizeInBytes = long("size_in_bytes").default(0).index()
    val entityId = long("entity_id").default(0).index()
    val fileAccess = enumeration<FileModifier>("modifier").index()
}