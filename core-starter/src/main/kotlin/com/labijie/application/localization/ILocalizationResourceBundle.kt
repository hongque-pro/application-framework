package com.labijie.application.localization

/**
 * @author Anders Xiao
 * @date 2023-12-07
 */
interface ILocalizationResourceBundle {
    /**
     * Return resource bundle include message source file.
     *
     * example: listOf("classpath:org/springframework/security/messages")
     */
    fun getResources() : List<String>
}

