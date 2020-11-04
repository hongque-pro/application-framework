package com.labijie.application.payment

import org.springframework.http.MediaType

data class CallbackResponse(
        val body:String,
        val mediaType: MediaType = MediaType.TEXT_PLAIN)