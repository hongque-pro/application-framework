/**
 * @author Anders Xiao
 * @date 2025-07-11
 */
package com.labijie.application.component.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.object-storage.file")
class FileSystemStorageProperties(
    var baseFolder: String = "",
    var enabled:Boolean = false
)