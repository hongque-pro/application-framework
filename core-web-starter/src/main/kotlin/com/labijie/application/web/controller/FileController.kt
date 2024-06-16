/**
 * @author Anders Xiao
 * @date 2023-12-11
 */
package com.labijie.application.web.controller

import com.labijie.application.component.IObjectStorage
import com.labijie.application.model.FileModifier
import com.labijie.application.model.ObjectPreSignUrl
import com.labijie.application.service.IFileIndexService
import com.labijie.application.service.TouchedFile
import com.labijie.infra.utils.ifNullOrBlank
import org.hibernate.validator.constraints.Length
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


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
        @RequestParam("modifier", required = true) modifier: FileModifier,
        @RequestParam("size", required = false) size: Long? = null
    ): TouchedFile {

        val name = UUID.randomUUID().toString().replace("-", "").lowercase()
        val ext = fileExtensions.orEmpty().trimStart('.')
        val normalizedFolder = folder.trim('/')
        val fullPath = "${normalizedFolder}/${name}.${ext}"
        return fileIndexService.touchFile(fullPath, modifier, size)
    }

}