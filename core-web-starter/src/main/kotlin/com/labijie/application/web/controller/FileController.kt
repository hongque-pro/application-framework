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
        @RequestParam("filePath", required = true) filePath: String,
        @RequestParam("modifier", required = true) modifier: FileModifier
    ): ObjectPreSignUrl {
        return fileIndexService.getFileUrl(filePath, modifier)
    }

    @PostMapping("/touch")
    fun touch(
        @RequestParam("folder", required = true) @Length(min = 1, max = 128) folder: String,
        @RequestParam("ext", required = false) fileExtensions: String?,
        @RequestParam("filename", required = false) filename: String?,
        @RequestParam("modifier", required = true) modifier: FileModifier,
        @RequestParam("short", required = false) short: Boolean = false
    ): TouchedFile {
        val normalizedFolder = folder.trim('/')
        val name = if(fileExtensions.isNullOrBlank() && !filename.isNullOrBlank()) {
            filename
        }else {
            val ext = fileExtensions.orEmpty().removePrefix(".").let {
                val suffix = if(it.contains('.')) FilenameUtils.getExtension(it) else it
                if(suffix.isNotBlank()) ".${suffix}" else ""
            }
            val name = if (short) ShortId.newId() else UUID.randomUUID().toString().replace("-", "").lowercase()
            "${name}${ext}"
        }

        val fullPath = "${normalizedFolder}/$name"
        return fileIndexService.touchFile(fullPath, modifier)
    }

}