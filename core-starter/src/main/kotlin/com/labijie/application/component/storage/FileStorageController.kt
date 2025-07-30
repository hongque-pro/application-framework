/**
 * @author Anders Xiao
 * @date 2025-07-11
 */
package com.labijie.application.component.storage

import com.labijie.application.BucketPolicy
import com.labijie.application.MimeUtils
import com.labijie.application.exception.StoredObjectNotFoundException
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import okio.Path.Companion.toPath
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.regex.Pattern


@Controller
@RequestMapping("/file-storage")
@PermitAll
class FileStorageController(private val fileSystemObjectStorage: FileSystemObjectStorage) {

    companion object {
        private val pattern = Pattern.compile("^/file-storage/(read|upload)/(public|private)/(.+)$")
        private val logger by lazy {
            LoggerFactory.getLogger(FileStorageController::class.java)
        }
    }

    private fun String.toBucketPolicy(): BucketPolicy? {
        if (this.equals(BucketPolicy.PUBLIC.toString(), ignoreCase = true)) {
            return BucketPolicy.PUBLIC
        }
        if (this.equals(BucketPolicy.PRIVATE.toString(), ignoreCase = true)) {
            return BucketPolicy.PRIVATE
        }
        return null
    }

    private fun HttpServletRequest.getFileKey(pattern: Pattern): String? {
        val path = this.requestURI.toPath().toString()
        val m = pattern.matcher(path)
        if (m.matches()) {
            return m.group(3)
        }
        return null
    }

    @RequestMapping("/upload/{modifier}/**", method = [RequestMethod.PUT, RequestMethod.POST])
    fun uploadFile(
        @PathVariable("modifier", required = true) modifier: String,
        request: HttpServletRequest
    ): ResponseEntity<Any> {

        val policy = modifier.toBucketPolicy() ?: return returnNotFound()
        val key = request.getFileKey(pattern) ?: return returnNotFound()

        fileSystemObjectStorage.uploadObject(key,
            request.inputStream, policy)

        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/read/{modifier}/**")
    fun readFile(
        @PathVariable("modifier", required = true) modifier: String, request: HttpServletRequest
    ): ResponseEntity<Any> {
        val policy = modifier.toBucketPolicy() ?: return returnNotFound("")

        return request.getFileKey(pattern)?.let { key ->
            try {
                val extension = FilenameUtils.getExtension(key)
                val bytes: ByteArray = fileSystemObjectStorage.getObject(key, policy)
                val headers = HttpHeaders()
                headers.contentLength = bytes.size.toLong()
                val mime = MimeUtils.getMimeByExtensions(extension)
                headers.contentType = MediaType.parseMediaType(mime)
                ResponseEntity(bytes, headers, HttpStatus.OK)
            } catch (_: StoredObjectNotFoundException) {
                val message = "Unable to found ${policy.toString().lowercase()} object: $key"
                logger.debug(message)
                returnNotFound(message)
            }
        } ?: returnNotFound("")
    }

    private fun returnNotFound(message: String? = null): ResponseEntity<Any> {
        if(message.isNullOrBlank()) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        val bytes = message.toByteArray(Charsets.UTF_8)
        val headers = HttpHeaders()
        headers.contentLength = bytes.size.toLong()
        headers.contentType = MediaType.TEXT_PLAIN
        return ResponseEntity(bytes, HttpStatus.NOT_FOUND)
    }
}