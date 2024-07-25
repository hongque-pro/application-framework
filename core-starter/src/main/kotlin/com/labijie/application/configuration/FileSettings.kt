/**
 * @author Anders Xiao
 * @date 2024-07-25
 */
package com.labijie.application.configuration

import java.time.Duration


data class FileSettings(
    var tempFileExpiration: Duration = Duration.ofHours(1)
)