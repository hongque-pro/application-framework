package com.labijie.application.testing

import org.apache.commons.io.FilenameUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Anders Xiao
 * @date 2024-08-19
 */
class FileExtensionsTester {

    @Test
    fun testExtensions() {
        assertEquals("", FilenameUtils.getExtension(""))
        assertEquals("", FilenameUtils.getExtension("jpg"))
        assertEquals("jpg", FilenameUtils.getExtension(".jpg"))
        assertEquals("jpg", FilenameUtils.getExtension("a.jpg"))
        assertEquals("jpg", FilenameUtils.getExtension(".a.jpg"))
        assertEquals(null, FilenameUtils.getExtension(null))
    }
}