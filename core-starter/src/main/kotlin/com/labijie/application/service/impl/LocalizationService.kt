package com.labijie.application.service.impl

import com.labijie.application.configure
import com.labijie.application.data.LocalizationCodeTable
import com.labijie.application.data.LocalizationLanguageTable
import com.labijie.application.data.LocalizationMessageTable
import com.labijie.application.data.pojo.LocalizationCode
import com.labijie.application.data.pojo.LocalizationLanguage
import com.labijie.application.data.pojo.LocalizationMessage
import com.labijie.application.data.pojo.dsl.LocalizationCodeDSL.insert
import com.labijie.application.data.pojo.dsl.LocalizationCodeDSL.selectByPrimaryKey
import com.labijie.application.data.pojo.dsl.LocalizationLanguageDSL.insert
import com.labijie.application.data.pojo.dsl.LocalizationLanguageDSL.selectByPrimaryKey
import com.labijie.application.data.pojo.dsl.LocalizationLanguageDSL.toLocalizationLanguageList
import com.labijie.application.data.pojo.dsl.LocalizationMessageDSL.insert
import com.labijie.application.data.pojo.dsl.LocalizationMessageDSL.selectByPrimaryKey
import com.labijie.application.data.pojo.dsl.LocalizationMessageDSL.updateByPrimaryKey
import com.labijie.application.executeReadOnly
import com.labijie.application.model.LocalizationMessages
import com.labijie.application.removeAfterTransactionCommit
import com.labijie.application.service.ILocalizationService
import com.labijie.caching.getOrSet
import com.labijie.caching.getOrSetSliding
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import org.apache.commons.lang3.LocaleUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import java.util.*

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
class LocalizationService(
    private val transactionTemplate: TransactionTemplate,
) : ILocalizationService {
    private fun Locale.getId() = this.toLanguageTag()

    private val cacheManager: MemoryCacheManager = MemoryCacheManager()

    private fun getCacheKey(locale: Locale, code: String): String {
        return "loc_${locale.getId()}:${code}"
    }

    private fun allLocales(): List<Locale> {
        return cacheManager.getOrSet("loc_all_locales", Duration.ofDays(365)) {
            transactionTemplate.executeReadOnly() {
                val list = LocalizationLanguageTable.selectAll()
                    .orderBy(LocalizationLanguageTable.locale)
                    .toLocalizationLanguageList()
                list.map { LocaleUtils.toLocale(it.locale) }
            }
        } ?: listOf()
    }

    private fun addLocaleIfNotExisted(locale: Locale, refreshCache: Boolean = true) {
        val localId = locale.getId()
        val existed = allLocales().any { locale.getId() == it.getId() }
        if (existed) {
            transactionTemplate.execute {
                val lang = LocalizationLanguageTable.selectByPrimaryKey(localId)
                if (lang == null) {
                    if (refreshCache) {
                        cacheManager.removeAfterTransactionCommit("all_locales")
                    }
                    val newLang = LocalizationLanguage().apply {
                        this.locale = localId
                        this.language = locale.language
                        this.country = locale.country
                    }
                    LocalizationLanguageTable.insert(newLang)
                }
            }
        }
    }

    private fun addOrUpdateMessage(
        code: String,
        message: String,
        locale: Locale,
        override: Boolean,
        refreshCache: Boolean = true
    ): Boolean {
        val count = transactionTemplate.execute {
            val existed = LocalizationMessageTable.selectByPrimaryKey(
                locale.getId(),
                code,
                LocalizationMessageTable.locale, LocalizationMessageTable.code
            )
            if (existed == null) {
                val newMessage = LocalizationMessage().apply {
                    this.locale = locale.getId()
                    this.code = code
                    this.message = message
                }
                LocalizationMessageTable.insert(newMessage).insertedCount
            } else if (override) {
                if (refreshCache) {
                    cacheManager.removeAfterTransactionCommit(getCacheKey(locale, code))
                }
                existed.message = message
                LocalizationMessageTable.updateByPrimaryKey(existed)
            } else {
                0
            }
        } ?: 0
        return count > 0
    }

    private fun addCodeIfNotExisted(code: String) {

        transactionTemplate.execute {
            val lc = LocalizationCodeTable.selectByPrimaryKey(code)
            if (lc == null) {
                val newLoc = LocalizationCode().apply { this.code = code }
                LocalizationCodeTable.insert(newLoc)
            }
        }
    }

    private fun setMessage(
        code: String,
        message: String,
        locale: Locale,
        override: Boolean,
        refreshCache: Boolean
    ): Boolean {
        if (code.isBlank()) {
            logger.warn("Localization code is blank.")
            return false
        }
        return transactionTemplate.execute {
            addLocaleIfNotExisted(locale, refreshCache)
            addCodeIfNotExisted(code)
            addOrUpdateMessage(code, message, locale, override, refreshCache)
        } ?: false
    }

    override fun setMessage(code: String, message: String, locale: Locale, override: Boolean): Boolean {
        return setMessage(code, message, locale, override, true)
    }

    override fun setMessages(properties: Properties, locale: Locale, override: Boolean): Int {
        return transactionTemplate.execute {
            var count = 0
            properties.forEach {
                val success = setMessage(it.key.toString(), it.value.toString(), locale, override, false)
                if (success) {
                    count++
                }
            }
            count
        } ?: 0
    }

    override fun getMessage(code: String, locale: Locale): String? {
        if (code.isBlank()) {
            return null
        }
        val key = getCacheKey(locale, code)
        return cacheManager.getOrSetSliding(key, Duration.ofMinutes(5)) {
            transactionTemplate.configure(isReadOnly = true).execute {
                LocalizationMessageTable.selectByPrimaryKey(
                    locale.getId(),
                    code,
                    LocalizationMessageTable.message
                )?.message
            }
        }
    }

    override fun getLocaleMessages(locale: Locale): LocalizationMessages {

        return transactionTemplate.executeReadOnly {

            val messagesQuery = LocalizationMessageTable
                .slice(LocalizationMessageTable.code, LocalizationMessageTable.message)
                .selectAll()
                .andWhere {
                    LocalizationMessageTable.locale eq locale.getId()
                }.alias("messages")

            val list = LocalizationCodeTable.join(
                messagesQuery,
                JoinType.LEFT,
                LocalizationCodeTable.code,
                messagesQuery[LocalizationMessageTable.code]
            )
                .slice(LocalizationCodeTable.code, messagesQuery[LocalizationMessageTable.message])
                .selectAll()
                .orderBy(LocalizationCodeTable.code)
                .toList()

            val messages = list.associate {
                it[LocalizationCodeTable.code] to it[messagesQuery[LocalizationMessageTable.message]].orEmpty()
            }

            LocalizationMessages(locale, messages)
        } ?: LocalizationMessages(locale)
    }

    override fun saveLocaleMessages(message: LocalizationMessages, override: Boolean) {
        transactionTemplate.execute {
            message.messages.forEach { (code, msg) ->
                setMessage(code, msg, message.locale, override)
            }
        }
    }

    override fun reload() {
        cacheManager.clear()
    }

}