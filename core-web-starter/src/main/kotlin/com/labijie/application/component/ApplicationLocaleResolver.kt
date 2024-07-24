/**
 * @author Anders Xiao
 * @date 2024-07-24
 */
package com.labijie.application.component

import com.labijie.application.service.ILocalizationService
import com.labijie.infra.oauth2.OAuth2Utils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.LocaleUtils
import org.springframework.web.servlet.LocaleResolver
import java.util.*


/**
 * @see org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
 */
class ApplicationLocaleResolver(private val localizationService: ILocalizationService) : LocaleResolver {

    override fun resolveLocale(request: HttpServletRequest): Locale {
        return getFromPrinciple() ?: getFromAcceptHeader(request, localizationService.allLocales()) ?: localizationService.getDefault()
    }

    override fun setLocale(request: HttpServletRequest, response: HttpServletResponse?, locale: Locale?) {
        throw UnsupportedOperationException(
            "Cannot change HTTP Accept-Language header - use a different locale resolution strategy"
        )
    }

    private fun getFromPrinciple(): Locale? {
        val principal = OAuth2Utils.currentTwoFactorPrincipalOrNull()
        return principal?.let {
            p->
            val language = p.attachedFields["lang"] ?:  p.attachedFields["language"] ?: p.attachedFields["locale"]
            val principleLocale = language?.let {
                try {
                    LocaleUtils.toLocale(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            principleLocale?.let {
                localizationService.findSupportedLocale(it)
            }
        }
    }

    private fun getFromAcceptHeader(request: HttpServletRequest, supportedLocales: List<Locale>): Locale? {
        if (request.getHeader("Accept-Language") == null) {
            return null
        }
        val requestLocales = request.locales

        while (requestLocales.hasMoreElements()) {
            val locale = requestLocales.nextElement() as Locale
            if (supportedLocales.contains(locale)) {
                return locale
            }

            val supportIterator: Iterator<*> = supportedLocales.iterator()

            while (supportIterator.hasNext()) {
                val supportedLocale = supportIterator.next() as Locale
                if (supportedLocale.country.isNullOrBlank() && supportedLocale.language == locale.language) {
                    return supportedLocale
                }
            }
        }

        return localizationService.findSupportedLocale(request.locale)
    }

}