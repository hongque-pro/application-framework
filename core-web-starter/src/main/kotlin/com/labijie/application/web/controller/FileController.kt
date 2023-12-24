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
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/files")
class FileController : ApplicationContextAware {
    private lateinit var context:ApplicationContext

    private val fileIndexService by lazy {
        context.getBean(IFileIndexService::class.java)
    }

    @GetMapping("/pre-sign")
    fun preSignRead(
        @RequestParam("filePath", required = true) filePath: String,
        @RequestParam("modifier", required = true) modifier: FileModifier
    ): ObjectPreSignUrl {

        return fileIndexService.getFileUrl(filePath, modifier)
    }

    @PostMapping("/touch")
    fun touch(
        @RequestParam("filePath", required = true) filePath: String,
        @RequestParam("modifier", required = true) modifier: FileModifier
    ): TouchedFile {

        return fileIndexService.touchFile(filePath, modifier)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}