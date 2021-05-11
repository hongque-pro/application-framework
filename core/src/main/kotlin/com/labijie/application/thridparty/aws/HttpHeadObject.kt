package com.labijie.application.thridparty.aws

class HttpHeadObject<T> internal constructor(val data: T, val headers: Map<String, String>)