package com.labijie.application.web

import java.io.*
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class BodyCachedHttpServletRequest(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {
    private var cachedBytes: ByteArray? = null

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        if (cachedBytes == null) {
            cacheInputStream()
        }
        return ByteArrayServletInputStream(cachedBytes!!)
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    @Throws(IOException::class)
    private fun cacheInputStream() {
        /* Cache the inputstream in order to read it multiple times. For
     * convenience, I use apache.commons IOUtils
     */
        val output = ByteArrayOutputStream()
        super.getInputStream().copyTo(output)
        cachedBytes = output.toByteArray()
    }

    class ByteArrayServletInputStream(bytes: ByteArray) : ServletInputStream() {
        /**
         * Return the underlying source stream (never `null`).
         */
        private val sourceStream: InputStream = ByteArrayInputStream(bytes)
        private var finished = false

        @Throws(IOException::class)
        override fun read(): Int {
            val data = sourceStream.read()
            if (data == -1) {
                finished = true
            }
            return data
        }

        @Throws(IOException::class)
        override fun available(): Int {
            return sourceStream.available()
        }

        @Throws(IOException::class)
        override fun close() {
            super.close()
            sourceStream.close()
        }

        override fun isFinished(): Boolean {
            return finished
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setReadListener(readListener: ReadListener) {
            throw UnsupportedOperationException()
        }
    }
}