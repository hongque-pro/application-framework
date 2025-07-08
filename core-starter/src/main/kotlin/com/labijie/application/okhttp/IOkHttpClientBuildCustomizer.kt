package com.labijie.application.okhttp

import okhttp3.OkHttpClient
import org.springframework.core.Ordered

/**
 * @author Anders Xiao
 * @date 2025/6/21
 */
interface IOkHttpClientBuildCustomizer: Ordered {
    override fun getOrder(): Int {
        return 0
    }
    fun customize(builder: OkHttpClient.Builder)
}