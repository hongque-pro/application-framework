package com.labijie.application.service.impl

import com.labijie.application.*
import com.labijie.application.data.LocalizationCodeTable
import com.labijie.application.data.LocalizationLanguageTable
import com.labijie.application.data.LocalizationMessageTable
import com.labijie.application.data.pojo.LocalizationCode
import com.labijie.application.data.pojo.dsl.LocalizationCodeDSL.selectByPrimaryKey
import com.labijie.application.data.pojo.dsl.LocalizationCodeDSL.upsert
import com.labijie.application.data.pojo.dsl.LocalizationLanguageDSL.selectMany
import com.labijie.application.data.pojo.dsl.LocalizationLanguageDSL.toLocalizationLanguageList
import com.labijie.application.data.pojo.dsl.LocalizationLanguageDSL.updateByPrimaryKey
import com.labijie.application.data.pojo.dsl.LocalizationMessageDSL.selectByPrimaryKey
import com.labijie.application.localization.ILocalizationChangedListener
import com.labijie.application.model.LocalizationMessages
import com.labijie.application.service.ILocalizationService
import com.labijie.caching.getOrSet
import com.labijie.caching.memory.MemoryCacheManager
import com.labijie.infra.orm.withoutSqlLog
import com.labijie.infra.utils.logger
import org.apache.commons.lang3.LocaleUtils
import org.jetbrains.exposed.sql.*
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.optionals.getOrNull

/**
 * @author Anders Xiao
 * @date 2023-12-06
 */
class JdbcLocalizationService(
    private val transactionTemplate: TransactionTemplate,
) : ILocalizationService, ApplicationContextAware {

    private val cacheManager = MemoryCacheManager()
    private val localePreferences: ConcurrentHashMap<String, Optional<Locale>> = ConcurrentHashMap()

    private var springContext : ApplicationContext? = null

    private val listeners by lazy {
        springContext?.getBeanProvider(ILocalizationChangedListener::class.java)?.orderedStream()?.toList() ?: listOf()
    }

    companion object {
        private const val ALL_LOCALES_CACHE_KEY = "loc:all_locales"
        private const val DEFAULT_LOCAL_KEY = "loc:default"
        private val DEFAULT_TIMEOUT =  Duration.ofDays(365)
        private val MESSAGE_TIMEOUT = Duration.ofDays(1)
    }

    private fun getLocaleCacheKey(locale: Locale): String {
        return "loc:lang:${locale.getId()}"
    }

    override fun getDefault(): Locale {
        return cacheManager.getOrSet(DEFAULT_LOCAL_KEY, DEFAULT_TIMEOUT) {
            val list = transactionTemplate.executeReadOnly {
                LocalizationLanguageTable.selectAll()
                    .orderBy(LocalizationLanguageTable.locale)
                    .toList()
            }

            val dataList = list?.toLocalizationLanguageList()
            val d = dataList?.firstOrNull { it.default } ?: dataList?.firstOrNull()
            d?.let {
                LocaleUtils.toLocale(d.locale)
            } ?: Locale.US
        } ?: Locale.US
    }

    override fun findSupportedLocale(locale: Locale): Locale? {
        return localePreferences.getOrPut(locale.getId()) {
            val l = findLocale(locale, allLocales())
            Optional.ofNullable(l)
        }.getOrNull()
    }

    override fun setDefault(locale: Locale): Boolean {
        val id = locale.getId()
        return this.transactionTemplate.execute {
            val list = LocalizationLanguageTable.selectAll()
                .orderBy(LocalizationLanguageTable.locale)
                .toLocalizationLanguageList()


            val newDefault = list.firstOrNull { it.locale.equals(id, ignoreCase = true) }
            if(newDefault == null) {
                return@execute false
            }

            var changed = false
            list.firstOrNull { it.default }?.let {
                old->
                changed = LocalizationLanguageTable.updateByPrimaryKey(old.locale) {
                    it[default] = false
                } > 0
            }

            changed = changed || LocalizationLanguageTable.updateByPrimaryKey(newDefault.locale) {
                it[default] = true
            } > 0

            if(changed) {
                cacheManager.removeAfterTransactionCommit(DEFAULT_LOCAL_KEY)
                syncDbTransactionCommitted {
                    listeners.forEach { it.onChanged() }
                }
            }

            true
        } ?: true
    }

    override fun allLocales(): List<Locale> {
        return cacheManager.getOrSet(ALL_LOCALES_CACHE_KEY, DEFAULT_TIMEOUT) {
            withoutSqlLog {
                transactionTemplate.executeReadOnly() {
                    val list = LocalizationLanguageTable.selectMany {
                        andWhere {
                            LocalizationLanguageTable.disabled eq false
                        }
                    }
                    list.map { LocaleUtils.toLocale(it.locale) }
                }
            }
        } ?: listOf()
    }

    private fun addLocaleIfNotExisted(locale: Locale, refreshCache: Boolean = true): Boolean {
        val localId = locale.getId()
        val existed = allLocales().any { locale.getId() == it.getId() }
        if (!existed) {
            return transactionTemplate.execute {
                val count = LocalizationLanguageTable.upsert {
                    it[LocalizationLanguageTable.locale] = localId
                    it[language] = locale.language
                    it[country] = locale.country
                }
                if (count.insertedCount > 0 && refreshCache) {
                    syncDbTransactionCommitted {
                       cacheManager.remove(ALL_LOCALES_CACHE_KEY)
                       localePreferences.clear()
                   }
                }
                count.insertedCount > 0
            } ?: false
        }
        return false
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
            if (existed == null || override) {
                LocalizationMessageTable.upsert {
                    it[LocalizationMessageTable.locale] = locale.getId()
                    it[LocalizationMessageTable.code] = code
                    it[LocalizationMessageTable.message] = message
                }

                if (refreshCache) {
                    cacheManager.removeAfterTransactionCommit(getLocaleCacheKey(locale))
                    syncDbTransactionCommitted {
                        listeners.forEach { it.onChanged() }
                    }
                }
                1
            } else {
                0
            }

        } ?: 0
        return count > 0
    }

    private fun addCodeIfNotExisted(code: String): Boolean {

        return transactionTemplate.execute {
            val lc = LocalizationCodeTable.selectByPrimaryKey(code)
            if (lc == null) {
                val newLoc = LocalizationCode().apply { this.code = code }
                LocalizationCodeTable.upsert(newLoc)
                true
            }else {
                false
            }
        } ?: false
    }

    override fun setMessage(
        code: String,
        message: String,
        locale: Locale,
        override: Boolean
    ): Boolean {
        if (code.isBlank()) {
            logger.warn("Localization code is blank.")
            return false
        }
        return transactionTemplate.execute {
            val localAdded = addLocaleIfNotExisted(locale)
            val codeAdded = addCodeIfNotExisted(code)
            val added = addOrUpdateMessage(code, message, locale, override)

            val changed = localAdded || codeAdded || added
            if(changed) {
                syncDbTransactionCommitted {
                    listeners.forEach { it.onChanged() }
                }
            }
            changed
        } ?: false
    }

    override fun setMessages(messages: Map<String, String>, locale: Locale, override: Boolean): Int {
        val addCoded = mutableSetOf<String>()
        val localAdded = mutableSetOf<String>()
        var newLocal = 0
        var messageCount = 0

        transactionTemplate.execute {
            if(localAdded.add(locale.getId()) && addLocaleIfNotExisted(locale, false))
            {
                newLocal++
            }

            messages.forEach {
                val code = it.key.toString()
                if(addCoded.add(code)){
                    addCodeIfNotExisted(code)
                }
                if(addOrUpdateMessage(code, it.value.toString(), locale, override, true)) {
                    messageCount++
                }
            }
            if(newLocal >0) {
                syncDbTransactionCommitted {
                    cacheManager.remove(ALL_LOCALES_CACHE_KEY)
                    localePreferences.clear()
                }
            }

            if(newLocal > 0 || messageCount > 0) {
                syncDbTransactionCommitted {
                    listeners.forEach { it.onChanged() }
                }
            }
        }
        return messageCount
    }


    override fun getMessage(code: String, locale: Locale): String? {
        if (code.isBlank()) {
            return null
        }
        val l = findSupportedLocale(locale) ?: getDefault()
        return getLocaleMessages(l).messages[code]
    }

    override fun getLocaleMessages(locale: Locale): LocalizationMessages {
        val key = getLocaleCacheKey(locale)
        return cacheManager.getOrSet(key, MESSAGE_TIMEOUT) {
            transactionTemplate.executeReadOnly {
                val messagesQuery = LocalizationMessageTable
                    .select(LocalizationMessageTable.code, LocalizationMessageTable.message)
                    .andWhere {
                        LocalizationMessageTable.locale eq locale.getId()
                    }.alias("messages")

                val list = LocalizationCodeTable.join(
                    messagesQuery,
                    JoinType.LEFT,
                    LocalizationCodeTable.code,
                    messagesQuery[LocalizationMessageTable.code]
                )
                    .select(LocalizationCodeTable.code, messagesQuery[LocalizationMessageTable.message])
                    .orderBy(LocalizationCodeTable.code)
                    .toList()

                val messages = list.associate {
                    it[LocalizationCodeTable.code] to it[messagesQuery[LocalizationMessageTable.message]]
                }

                LocalizationMessages(locale, messages)
            }
        }!!
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
        localePreferences.clear()
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        springContext = applicationContext
    }

}