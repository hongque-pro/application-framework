package com.labijie.application

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

object SSLUtilities {

    val NoneValidationTrustManager:DisableValidationTrustManager by lazy {
        DisableValidationTrustManager()
    }

    class DisableValidationTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            x509Certificates: Array<X509Certificate>,
            s: String
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            x509Certificates: Array<X509Certificate>,
            s: String
        ) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    @Throws(FileNotFoundException::class)
    fun createSSLSocketFactory(filePath:String, password: String?): SSLSocketFactory {
        var stream = Thread.currentThread().contextClassLoader.getResourceAsStream(filePath)
        if(stream == null){
            val file = File(filePath)
            if(file.exists()){
                stream = FileInputStream(file)
            }
        }

        val s = stream ?: throw FileNotFoundException("Resource or file was not found. ${System.lineSeparator()} path: $filePath")
        val clientStore = KeyStore.getInstance("PKCS12")

        s.use {
            clientStore.load(it, password?.toCharArray());
        }

        val sslCtx = SSLContext.getInstance("TLS")
        val kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
            init(clientStore, password?.toCharArray())
        }
        val keymanagers = kmfactory.keyManagers
        sslCtx.init(keymanagers, arrayOf(NoneValidationTrustManager), null)
        return sslCtx.socketFactory
    }
}