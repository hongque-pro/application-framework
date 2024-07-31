/**
 * @author Anders Xiao
 * @date 2024-07-26
 */
package com.labijie.application.component

import com.labijie.application.buildVersion
import com.labijie.application.toGitProperties
import com.labijie.infra.oauth2.OAuth2Utils
import com.labijie.infra.utils.toLocalDateTime


class WebBootPrinter : IBootPrinter {
    override fun appendBootMessages(messageBuilder: StringBuilder) {
        val gitProps = OAuth2Utils.getInfraOAuth2GitProperties().toGitProperties()
        messageBuilder.appendLine("infra oauth2 version: ${gitProps.buildVersion}")
        messageBuilder.appendLine("infra oauth2 commit: ${gitProps.commitTime?.toLocalDateTime()?.toLocalDate()}")
    }
}