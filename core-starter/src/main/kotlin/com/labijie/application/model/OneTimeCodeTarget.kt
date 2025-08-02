package com.labijie.application.model

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */
class OneTimeCodeTarget {
    enum class Channel {
        Email,
        Phone,
    }

    var channel: Channel = Channel.Email

    /**
     * phone number or email address
     */
    var contact: String = ""

    companion object {
        fun fromEmail(email: String): OneTimeCodeTarget {
            return OneTimeCodeTarget().apply {
                channel = Channel.Email
                contact = email
            }
        }

        fun fromPhone(phoneNumber: String): OneTimeCodeTarget {
            return OneTimeCodeTarget().apply {
                channel = Channel.Email
                contact = phoneNumber
            }
        }
    }
}