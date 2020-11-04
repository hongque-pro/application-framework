package com.labijie.application.okhttp

import okhttp3.OkHttpClient

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
interface IOkHttpClientCustomizer {
    fun customize(builder: OkHttpClient.Builder)
}