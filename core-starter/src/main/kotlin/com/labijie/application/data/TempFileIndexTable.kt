/**
 * @author Anders Xiao
 * @date 2024-07-25
 */
package com.labijie.application.data

import com.labijie.application.model.FileModifier
import com.labijie.infra.orm.SimpleLongIdTable


object TempFileIndexTable : SimpleLongIdTable("file_indices_temp") {
    val path = varchar("path", 256).uniqueIndex()
    val timeCreated = long("time_created").index()
    val sizeInBytes = long("size_in_bytes").default(0)
    val fileAccess = enumeration<FileModifier>("modifier")
    val timeExpired = long("time_expired").index()
    val expirationSeconds = long("expiration_seconds")
}