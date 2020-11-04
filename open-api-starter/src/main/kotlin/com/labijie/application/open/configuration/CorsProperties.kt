package com.labijie.application.open.configuration

class CorsProperties {
    /**
     * allowed-origins 测试：
     * ALL： 允许 pathPattern 匹配的路径跨域 （*）
     * PARTNER: 允许 pathPattern 匹配的路径根据合作伙伴配置的域名访问
     */
    var allowedOriginsPolicy: CorsPolicy = CorsPolicy.ALL
    var pathPattern: String = "/open-jsapi/**"

    /**
     * 配置跨域的 max-age (单位秒)， 默认 1800 秒，即 30 分钟
     */
    var maxAge: Long = 1800
}