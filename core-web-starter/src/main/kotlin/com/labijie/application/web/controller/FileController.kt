/**
 * @author Anders Xiao
 * @date 2023-12-11
 */
package com.labijie.application.web.controller

import com.labijie.application.BucketPolicy
import com.labijie.application.component.GenerationURLPurpose
import com.labijie.application.component.IObjectStorage
import com.labijie.application.model.FileModifier
import com.labijie.application.model.TouchFileResponse
import com.labijie.application.service.IFileIndexService
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/files")
class FileController : ApplicationContextAware {
    private lateinit var context:ApplicationContext

    private val objectStorage by lazy {
        context.getBean(IObjectStorage::class.java)
    }

    private val fileIndexService by lazy {
        context.getBean(IFileIndexService::class.java)
    }

    @PostMapping("/touch")
    fun touch(
        @RequestParam("filePath", required = true) filePath: String,
        @RequestParam("modifier", required = true) modifier: FileModifier
    ) {
        val bucketPolicy = if(modifier == FileModifier.Public) BucketPolicy.PUBLIC else BucketPolicy.PRIVATE
        val fileIndex = fileIndexService.touchFile(filePath, modifier)
        val url = objectStorage.generateObjectUrl(filePath, bucketPolicy, GenerationURLPurpose.Write)
        TouchFileResponse(fileIndex.id, filePath, url.url, url.timeoutMills)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }
}