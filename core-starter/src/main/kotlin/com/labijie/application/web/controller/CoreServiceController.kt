package com.labijie.application.web.controller

import com.labijie.application.web.annotation.ResponseWrapped
import com.labijie.application.web.service.ITempFileService
import com.labijie.insurance.core.model.TempFileRequest
import com.labijie.insurance.core.model.TempFileResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 *
 * @author lishiwen
 * @date 20-1-21
 * @since JDK1.8
 */
@RestController
@ResponseWrapped
@RequestMapping("/commons")
class CoreServiceController(
    private val tempFileService: ITempFileService) {

    @PostMapping("/touch-file")
    fun saveTempFile(@RequestBody file: TempFileRequest): TempFileResult {
        val id = UUID.randomUUID().toString()
        val fileName = "${file.folder.trim().trim('/')}/$id.${file.fileExtensions.trim().trimStart('.').toLowerCase()}"
        val success = tempFileService.saveTempFile(fileName, file.fileType)
        return TempFileResult(success, fileName)
    }
}