package com.labijie.application.open

import com.labijie.application.HttpFormUrlCodec

data class OpenApiRequest(
    val queryString: String,
    val bodyString: String
) {

    val appId: String by lazy {
        this.signAndData.second.getOrDefault(OpenSignatureUtils.AppIdField, "")
    }

    val signAndData: Pair<String, Map<String, String>> by lazy {
        this.getDataCore()
    }

    private fun getDataCore(): Pair<String, Map<String, String>> {
         val data = HttpFormUrlCodec.decode(queryString).toSingleValueMap()
        val sign = data.remove("sign").orEmpty()
        data["body"] = bodyString

        return Pair(sign,  data.toSortedMap())
    }
}