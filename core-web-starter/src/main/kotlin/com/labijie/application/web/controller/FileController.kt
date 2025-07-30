/**
 * @author Anders Xiao
 * @date 2023-12-11
 */
package com.labijie.application.web.controller

import com.labijie.application.model.FileModifier
import com.labijie.application.model.ObjectPreSignUrl
import com.labijie.application.service.IFileIndexService
import com.labijie.application.service.TouchedFile
import com.labijie.infra.utils.ShortId
import org.apache.commons.io.FilenameUtils
import org.hibernate.validator.constraints.Length
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/files")
class FileController(private val fileIndexService: IFileIndexService) {

    @GetMapping("/pre-sign")
    fun preSignRead(
        @RequestParam("filePath", required = true) filePath: String
    ): ObjectPreSignUrl {
        return fileIndexService.getFileUrl(filePath)
    }

    @PostMapping("/touch")
    fun touch(
        @RequestParam("folder", required = true) @Length(min = 1, max = 128) folder: String,
        @RequestParam("ext", required = false) fileExtensions: String?,
        @RequestParam("modifier", required = true) modifier: FileModifier,
        @RequestParam("short", required = false) short: Boolean = false,
        @RequestParam("mime", required = false) mime: String? = null,
    ): TouchedFile {
        val normalizedFolder = folder.trim('/')
        val ext = fileExtensions.orEmpty().removePrefix(".").let {
            val suffix = if(it.contains('.')) FilenameUtils.getExtension(it) else it
            if(suffix.isNotBlank()) ".${suffix}" else ""
        }
        val id = if (short) ShortId.newId() else UUID.randomUUID().toString().replace("-", "").lowercase()
        val name = "${id}${ext}"

        val fullPath = "${normalizedFolder}/$name"
        return fileIndexService.touchFile(fullPath, modifier, mime = mime)
    }

}