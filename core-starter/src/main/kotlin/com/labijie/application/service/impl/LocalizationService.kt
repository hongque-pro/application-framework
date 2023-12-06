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
import com.labijie.application.data.pojo.dsl.LocalizationMessageDSL.insert
import com.labijie.application.data.pojo.dsl.LocalizationMessageDSL.selectByPrimaryKey
import com.labijie.application.data.pojo.dsl.LocalizationMessageDSL.updateByPrimaryKey
import com.labijie.application.executeReadOnly
import com.labijie.application.model.LocalizationMessages
import com.labijie.application.service.ILocalizationService
import com.labijie.caching.getOrSetSliding
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.IIdGenerator
import com.labijie.infra.utils.ifNullOrBlank
import com.labijie.infra.utils.logger
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
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

    private fun addLocaleIfNotExisted(locale: Locale) {
        val localId = locale.getId()
        transactionTemplate.execute {
            val lang = LocalizationLanguageTable.selectByPrimaryKey(localId)
            if (lang == null) {
                val newLang = LocalizationLanguage().apply {
                    this.locale = localId
                    this.language = locale.language
                    this.country = locale.country
                }
                LocalizationLanguageTable.insert(newLang)
            }
        }
    }

    private fun addOrUpdateMessage(code: String, message: String, locale: Locale, override: Boolean) {
        transactionTemplate.execute {
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
                existed.message = message
                LocalizationMessageTable.updateByPrimaryKey(existed)
            } else {
                0
            }
        }
    }

    private fun addCodeIfNotExisted(code: String) {

        transactionTemplate.execute {
            val lc = LocalizationCodeTable.selectByPrimaryKey(code)
            if(lc == null) {
                val newLoc = LocalizationCode().apply { this.code = code }
                LocalizationCodeTable.insert(newLoc)
            }
        }
    }

    override fun setMessage(code: String, message: String, locale: Locale, override: Boolean) {
        if (code.isBlank()) {
            logger.warn("Localization code is blank.")
            return
        }
        transactionTemplate.execute {
            addLocaleIfNotExisted(locale)
            addCodeIfNotExisted(code)
            addOrUpdateMessage(code, message, locale, override)
        }
    }

    override fun getMessage(code: String, locale: Locale): String? {
        if (code.isBlank()) {
            return null
        }
        return cacheManager.getOrSetSliding("${locale.getId()}:${code}", Duration.ofMinutes(6)) {
            transactionTemplate.configure(isReadOnly = true).execute {
                LocalizationMessageTable.selectByPrimaryKey(
                    locale.getId(),
                    code,
                    LocalizationMessageTable.message
                )?.message
            }
        }
    }

    override fun getLocaleMessages(locale: Locale, includeBlank: Boolean): LocalizationMessages {
         return transactionTemplate.executeReadOnly {
            val list = LocalizationCodeTable.join(LocalizationMessageTable, JoinType.LEFT,
                onColumn = LocalizationCodeTable.code,
                otherColumn = LocalizationMessageTable.code)
                .slice(LocalizationCodeTable.code, LocalizationMessageTable.message)
                .selectAll()
                .andWhere { LocalizationMessageTable.locale eq locale.getId() }
                .orderBy(LocalizationCodeTable.code)
                .toList()

             val messages = list.associate {
                 it[LocalizationCodeTable.code] to it[LocalizationMessageTable.message]
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

}