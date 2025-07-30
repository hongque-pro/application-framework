package com.labijie.application

import com.fasterxml.jackson.core.type.TypeReference
import com.labijie.application.copier.BeanCopierUtils
import com.labijie.application.service.ILocalizationService
import com.labijie.caching.ICacheManager
import com.labijie.infra.SecondIntervalTimeoutTimer
import com.labijie.infra.json.JacksonHelper
import org.springframework.boot.info.GitProperties
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.support.*
import org.springframework.util.LinkedCaseInsensitiveMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriBuilder
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.net.URLEncoder
import java.nio.ByteOrder
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.reflect.KClass


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */

fun parseDateTime(
    timeString: String,
    format: String = "yyyy-MM-dd HH:mm:ss",
    zone: ZoneOffset = ZoneOffset.ofHours(8),
    defaultValue: Long = 0
): Long {
    if (timeString.isBlank()) {
        return defaultValue
    }
    return try {
        val dt = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern(format))
        OffsetDateTime.of(dt.year, dt.monthValue, dt.dayOfMonth, dt.hour, dt.minute, dt.second, 0, zone)
            .toInstant().toEpochMilli()
    } catch (e: DateTimeParseException) {
        defaultValue
    }
}

/**
 * 自动转换为正则表达式内可直接使用的字符串（自动插入转义字符）。
 */
fun String.escapeForRegex(): String {
    if (this.isBlank()) {
        return this
    }

    val builder = StringBuilder();
    //Regex.Escape 不会对]、} 处理。
    this.forEach {
        when (it) {
            '\\',
            '(',
            ')',
            '[',
            ']',
            '{',
            '}',
            '.',
            '*',
            '+',
            '?',
            '|',
            '^',
            '$' -> {
                builder.append('\\')
                builder.append(it)
            }

            else ->
                builder.append(it)
        }
    }
    return builder.toString()
}

fun <T : Throwable> Throwable.getCauseFromChain(clazz: KClass<T>): T? {
    var cause: Throwable? = this

    while (cause != null) {
        if (clazz.isInstance(cause)) {
            break
        }
        cause = if (cause.cause == null || cause === cause.cause || cause::class.java == cause.cause!!::class.java) {
            null
        } else {
            cause.cause
        }
    }
    @Suppress("UNCHECKED_CAST")
    return cause as? T
}

fun String.toKebabCase(): String {
    val regex = Regex("([a-z])([A-Z])")
    return this.replace(regex) {
        "${it.groupValues[1]}-${it.groupValues[2].lowercase()}"
    }
}

public fun String.paddingLeftAndRight(totalLength: Int, padChar: Char = ' '): String {
    if (this.length >= totalLength) {
        return this
    }
    val sideLen = (totalLength - length) / 2
    val str = this.padStart(this.length + sideLen, padChar)
    return str.padEnd(str.length + sideLen, padChar)
}

fun Any.toObjectMap(): Map<String, Any?> {
    val map = this as? Map<*, *>
    if (map != null) {
        return map.entries.map {
            it.key.toString() to it.value
        }.toObjectMap()
    }
    return JacksonHelper.defaultObjectMapper.convertValue(
        this,
        object : TypeReference<Map<String, Any?>>() {})
}

fun Any.formUrlEncode(urlSafeValue: Boolean = true): String {
    val map = (this as? Map<*, *>) ?: this.toObjectMap()
    return map.entries.filter { !(it.value?.toString().isNullOrBlank()) && !(it.value?.toString().isNullOrEmpty()) }
        .joinToString("&") {
            "${it.key}=${
                if (urlSafeValue) URLEncoder.encode(
                    it.value.toString(),
                    Charsets.UTF_8.name()
                ) else it.value
            }"
        }
}

fun Any.queryStringEncode(): String {
    val map = (this as? Map<*, *>) ?: this.toObjectMap()
    return QueryStringEncoder(null).apply {
        map.forEach {
            val name = it.key?.toString()
            val value = it.value?.toString()
            if (!name.isNullOrBlank() && !value.isNullOrBlank()) {
                this.addParam(name, value)
            }
        }

    }.toString().removePrefix("?")
}

fun <T : Any> T.propertiesFrom(source: Any): T {
    BeanCopierUtils.copyProperties(source, this)
    return this
}

inline fun <reified T : Enum<*>> Number.toEnum(): T {
    return this.toEnum(T::class.java)
}

fun <T : Enum<*>> Number.toEnum(type: Class<T>): T {
    val v = getEnumFromNumber(type, this)
        ?: throw IllegalStateException("$this (${this::class.java.simpleName}) is not a valid value type for enum ${type.simpleName}")
    @Suppress("UNCHECKED_CAST")
    return v as T
}

inline fun <reified T : Enum<*>> String.toEnum(): T {
    return getEnumFromString(T::class.java, this) as T
}

fun getEnumFromNumber(enumClass: Class<*>, value: Number, ignoreType: Boolean = true): Enum<*>? {
    val enumClz = enumClass.enumConstants.map {
        it as Enum<*>
    }
    return enumClz.firstOrNull {
        val desc = (it as? IDescribeEnum<*>)
        if (desc != null) {
            if (ignoreType) {
                BigDecimal(it.code.toString()) == BigDecimal(value.toString())
            } else {
                it.code == value && it.code::class.java == value::class.java
            }
        } else {
            it.ordinal == value.toInt()
        }
    }
}

fun getEnumFromString(enumClass: Class<*>, value: String, ignoreCase: Boolean = false): Enum<*>? {
    val enumClz = enumClass.enumConstants.map {
        it as Enum<*>
    }
    return enumClz.firstOrNull {
        it.name.equals(value, ignoreCase = ignoreCase)
    }
}

val Class<*>.isNumeric: Boolean
    get() = NumericClasses.contains(this)

val JAVA_LONG = Class.forName("java.lang.Long")
val JAVA_DOUBLE = Class.forName("java.lang.Double")
val JAVA_FLOAT = Class.forName("java.lang.Float")
val JAVA_INT = Class.forName("java.lang.Integer")
val JAVA_SHORT = Class.forName("java.lang.Short")
val JAVA_BYTE = Class.forName("java.lang.Byte")

val NumericClasses = arrayOf(
    JAVA_LONG,
    JAVA_DOUBLE,
    JAVA_FLOAT,
    JAVA_INT,
    JAVA_SHORT,
    JAVA_BYTE,
    Int::class.java,
    Short::class.java,
    Byte::class.java,
    Long::class.java,
    BigInteger::class.java,
    Float::class.java,
    BigDecimal::class.java
)

fun String.maskEmail(): String {
    if (this.isBlank()) {
        return ""
    }
    if (!this.contains('@')) {
        return this.mask(4)
    }
    val segments = this.split('@')
    return "${segments[0].mask(4)}@${segments[1]}"
}

fun String.maskPhone(): String {
    if (this.isBlank()) {
        return ""
    }
    if (this.length < 11) {
        return this.mask(4)
    }
    return "${this.substring(0..2).padEnd(this.length - 4, '*')}${this.substring(this.length - 4)}"
}

fun String.mask(maskLength: Int = 6, maskSymbol: String = "*"): String {
    if (this.isBlank()) {
        return this
    }
    val len = this.length
    val pamaone = len / 2
    val pamatwo = pamaone - 1
    val pamathree = len % 2
    val stringBuilder = StringBuilder()
    if (len <= 2) {
        if (pamathree == 1) {
            return maskSymbol
        }
        stringBuilder.append(maskSymbol)
        stringBuilder.append(this[len - 1])
    } else {
        if (pamatwo <= 0) {
            stringBuilder.append(this.substring(0, 1))
            stringBuilder.append(maskSymbol)
            stringBuilder.append(this.substring(len - 1, len))

        } else if (pamatwo >= maskLength / 2 && (maskLength + 1) != len) {
            val pamafive = (len - maskLength) / 2
            stringBuilder.append(this.substring(0, pamafive))
            for (i in 0 until maskLength) {
                stringBuilder.append(maskSymbol)
            }
            if (pamathree == 0 && maskLength / 2 == 0 || pamathree != 0 && (maskLength % 2) != 0) {
                stringBuilder.append(this.substring(len - pamafive, len))
            } else {
                stringBuilder.append(this.substring(len - (pamafive + 1), len))
            }
        } else {
            val pamafour = len - 2
            stringBuilder.append(this.substring(0, 1))
            for (i in 0 until pamafour) {
                stringBuilder.append(maskSymbol)
            }
            stringBuilder.append(this.substring(len - 1, len))
        }
    }
    return stringBuilder.toString()

}


fun ByteArray?.toUTF8StringOrEmpty(): String {
    return if (this != null && this.isNotEmpty()) {
        this.toString(Charsets.UTF_8)
    } else {
        ""
    }
}


fun <T : UriBuilder> T.queryParams(params: Map<String, *>): T {
    params.forEach { (t, u) ->
        if (u != null) {
            this.queryParam(t, u)
        }
    }
    return this
}

fun TransactionTemplate.configure(
    isReadOnly: Boolean = false,
    propagation: Propagation = Propagation.REQUIRED,
    isolationLevel: Isolation = Isolation.DEFAULT,
    timeout: Int = TransactionDefinition.TIMEOUT_DEFAULT
): TransactionTemplate {
    val definition = DefaultTransactionDefinition().apply {
        this.isReadOnly = isReadOnly
        this.isolationLevel = isolationLevel.value()
        this.propagationBehavior = propagation.value()
        this.timeout = timeout
    }
    return TransactionTemplate(this.transactionManager!!, definition)
}

fun <T> TransactionTemplate.executeReadOnly(
    propagation: Propagation = Propagation.REQUIRED,
    isolationLevel: Isolation = Isolation.DEFAULT,
    timeout: Int = TransactionDefinition.TIMEOUT_DEFAULT,
    action: TransactionCallback<T>
): T? {
    val definition = DefaultTransactionDefinition().apply {
        this.isReadOnly = true
        this.isolationLevel = isolationLevel.value()
        this.propagationBehavior = propagation.value()
        this.timeout = timeout
    }
    val tm = this.transactionManager ?: throw IllegalStateException("Transaction manager was unavailable.")
    return TransactionTemplate(tm, definition).execute(action)
}

inline fun <reified TKey : Any, reified TValue : Any> RestTemplate.exchangeForMap(
    url: String,
    method: HttpMethod,
    requestEntity: HttpEntity<*> = HttpEntity.EMPTY,
    uriVariables: Map<String, *>? = null
): ResponseEntity<Map<TKey, TValue>> {
    val mapType = object : ParameterizedTypeReference<Map<TKey, TValue>>() {}
    return this.exchange(url, method, requestEntity, mapType, uriVariables ?: mapOf<String, Any>())
}

inline fun <reified TItem : Any> RestTemplate.exchangeForList(
    url: String,
    method: HttpMethod,
    requestEntity: HttpEntity<*> = HttpEntity.EMPTY,
    uriVariables: Map<String, *>? = null
): ResponseEntity<List<TItem>> {
    val mapType = object : ParameterizedTypeReference<List<TItem>>() {}
    return this.exchange(url, method, requestEntity, mapType, uriVariables ?: mapOf<String, Any>())
}

fun ByteArray.toDouble(endian: ByteOrder = ByteOrder.BIG_ENDIAN) {
    BytesUtils.getDouble(this, endian)
}

fun ByteArray.toLong(endian: ByteOrder = ByteOrder.BIG_ENDIAN) {
    BytesUtils.getLong(this, endian)
}

fun ByteArray.toInt(endian: ByteOrder = ByteOrder.BIG_ENDIAN) {
    BytesUtils.getInt(this, endian)
}

fun Long.toByteArray(endian: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    val byteArray = ByteArray(8)
    BytesUtils.putLong(this, byteArray, endian)
    return byteArray
}

fun Int.toByteArray(endian: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    val byteArray = ByteArray(8)
    BytesUtils.putInt(this, byteArray, endian)
    return byteArray
}

fun Double.toByteArray(endian: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    val byteArray = ByteArray(8)
    BytesUtils.putDouble(this, byteArray, endian)
    return byteArray
}

fun Int?.orDefault(value: Int = 0): Int {
    return this ?: value
}

fun Float?.orDefault(value: Float = 0f): Float {
    return this ?: value
}

fun Double?.orDefault(value: Double = 0.0): Double {
    return this ?: value
}

fun BigDecimal?.orDefault(value: BigDecimal = BigDecimal.ZERO): BigDecimal {
    return this ?: value
}

fun String?.orDefault(value: String = ""): String {
    return this ?: value
}

fun Byte?.orDefault(value: Byte = 0): Byte {
    return this ?: value
}

fun Long?.orDefault(value: Long = 0): Long {
    return this ?: value
}

fun Short?.orDefault(value: Short = 0): Short {
    return this ?: value
}

fun BigInteger?.orDefault(value: BigInteger = BigInteger.ZERO): BigInteger {
    return this ?: value
}

fun ByteArray?.orDefault(value: ByteArray = ByteArray(0)): ByteArray {
    return this ?: value
}


fun ByteArray?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}


fun ICacheManager.removeAfterTransactionCommit(key: String, region: String? = null, delay: Duration = Duration.ZERO) {
    val sync = CacheRemoveTransactionSynchronization(this, key, region, delay)
    TransactionSynchronizationManager.registerSynchronization(sync)
}

internal class CacheRemoveTransactionSynchronization(
    private val cacheManager: ICacheManager,
    private val key: String,
    private val region: String?,
    private val delay: Duration
) : TransactionSynchronization {
    override fun afterCommit() {
        if (delay.seconds > 0) {
            SecondIntervalTimeoutTimer.newTimeout(delay.toMillis()) {
                this.cacheManager.remove(key, region)
            }
        } else {
            cacheManager.remove(key, region)
        }
    }
}

private var localeService: ILocalizationService? = null

private fun localeService(): ILocalizationService? {
    val service = localeService
    if (service == null) {
        localeService = if (SpringContext.isInitialized) {
            SpringContext.current.getBean(ILocalizationService::class.java)
        } else null
        return localeService
    }
    return service
}

fun defaultLocale(): Locale {
    val service = localeService()
    return service?.getDefault() ?: Locale.getDefault() ?: Locale.US
}

fun findLocale(locale: Locale, supportedLocales: List<Locale>): Locale? {
    return supportedLocales.firstOrNull { it.getId().equals(locale.getId(), ignoreCase = true) }
        ?: supportedLocales.firstOrNull {
            it.country.isNullOrBlank() && (locale.language ?: "").equals(
                it.language,
                ignoreCase = true
            )
        }
        ?: supportedLocales.firstOrNull { (locale.language ?: "").equals(it.language, ignoreCase = true) }
}

fun localeMessage(locale: Locale, code: String, vararg args: Any): String {
    return if (SpringContext.isInitialized) {
        SpringContext.current.getMessage(code, args, null, locale) ?: throw NoSuchMessageException(code)
    } else throw ApplicationRuntimeException("Spring context is NULL")
}

fun localeMessage(defaultMessage: String, code: String, vararg args: Any): String {
    return localeMessage(defaultMessage, LocaleContextHolder.getLocale(), code, *args)
}

fun localeMessage(defaultMessage: String, locale: Locale, code: String, vararg args: Any): String {
    return if (SpringContext.isInitialized) {
        SpringContext.current.getMessage(code, args, defaultMessage, locale) ?: defaultMessage
    } else defaultMessage
}

fun localeErrorMessage(errorCode: String, defaultMessage: String? = null): String {
    return localeErrorMessage(errorCode, null, defaultMessage)
}

fun localeErrorMessage(errorCode: String, args: Collection<Any>? = null, defaultMessage: String? = null): String {
    val locale = LocaleContextHolder.getLocale()

    val e = if (SpringContext.isInitialized) {
        if (SpringContext.errorRegistry.isLocalizationEnabled()) {
            SpringContext.current.getMessage(
                "app.err.${errorCode}",
                args?.toTypedArray(),
                defaultMessage,
                locale
            ) ?: defaultMessage
        } else {
            SpringContext.errorRegistry.getErrorMessage(errorCode,locale) ?: defaultMessage
        }
    } else defaultMessage

    return e ?: errorCode.replace("_", " ")
}

fun <V> Map<out String, V>.toCaseInsensitiveMap(): MutableMap<String, V> {
    val map = LinkedCaseInsensitiveMap<V>(this.size)
    this.forEach {
        map[it.key] = it.value
    }
    return map
}

fun <V> caseInsensitiveMapOf(vararg pairs: Pair<String, V>): MutableMap<String, V> {
    return LinkedCaseInsensitiveMap<V>(pairs.size).apply {
        pairs.forEach {
            this[it.first] = it.second
        }
    }
}

inline fun <reified V> caseInsensitiveMapOf(expectedSize: Int = 0) = LinkedCaseInsensitiveMap<V>(expectedSize)


fun syncDbTransactionCommitted(commit: () -> Unit) {
    TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
        override fun afterCommit() {
            commit.invoke()
        }
    })
}

fun syncDbTransactionRollback(rollback: () -> Unit) {
    TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
        override fun afterCompletion(status: Int) {
            if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                rollback.invoke()
            }
        }
    })
}

enum class DbTransactionState {
    Unknown,
    Committed,
    Rollback,
}

fun syncDbTransactionCompleted(completion: (status: DbTransactionState) -> Unit) {
    TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
        override fun afterCompletion(status: Int) {
            val s = when (status) {
                TransactionSynchronization.STATUS_ROLLED_BACK -> {
                    DbTransactionState.Rollback
                }

                TransactionSynchronization.STATUS_COMMITTED -> {
                    DbTransactionState.Committed
                }

                else -> DbTransactionState.Unknown
            }
            completion.invoke(s)
        }
    })
}


fun Locale.getId(): String = this.toLanguageTag().orEmpty()

@Throws(IOException::class)
fun <T : Any> loadResources(
    name: String, classLoader: ClassLoader?,
    filter: ((InputStream) -> T?)? = null
): T? {
    val systemResources: Enumeration<URL> =
        (classLoader ?: ClassLoader.getSystemClassLoader()).getResources(name)
    while (systemResources.hasMoreElements()) {
        systemResources.nextElement().openStream().use { stream ->
            val content: T? = filter?.invoke(stream)
            return content
        }
    }
    return null
}

/**
 * Git build version
 */
val GitProperties.buildVersion: String
    get() = this.get("build.version").orEmpty()

/**
 * Gradle project name (Auto gen if use infra-gradle-plugin)
 */
val GitProperties.projectGroup: String
    get() = this.get("project.group").orEmpty()

/**
 * Gradle project name (Auto gen if use infra-gradle-plugin)
 */
val GitProperties.projectName: String
    get() = this.get("project.name").orEmpty()

/**
 * Gradle project version (Auto gen if use infra-gradle-plugin)
 */
val GitProperties.projectVersion: String
    get() = this.get("project.version").orEmpty()


fun Properties.toGitProperties(): GitProperties {
    val gitValues = Properties().also { target ->
        this.forEach {
            if (it.value != null) {
                target.setProperty(it.key.toString().removePrefix("git."), it.value?.toString())
            }
        }
    }
    return GitProperties(gitValues)
}

/**
 * Get git properties from resource.
 *
 * If <strong>infra-gradle-plugin</strong> used, GitPropertiesFile will be "git-info/git.properties", other tool will be "git.properties"
 *
 * @param packageClass The resource belong package.
 * @param gitPropertiesFile Git properties resource file name. <br/> Default: "git-info/git.properties"
 * @param filter Filter git properties from resource if found multiple, for example: <br/> it[["project.Group"]] == "com.labijie.com"
 */
fun getGitProperties(
    packageClass: Class<*>,
    gitPropertiesFile: String = "git-info/git.properties",
    filter: ((properties: Properties) -> Boolean)? = null
): GitProperties? {
    if (gitPropertiesFile.isBlank()) {
        throw IllegalArgumentException("Git properties file can not be blank.")
    }
    return try {
        loadResources(gitPropertiesFile, packageClass.classLoader) {
            val properties = Properties().apply {
                this.load(it)
            }
            if (filter?.invoke(properties) != false) {
                properties.toGitProperties()
            } else null
        }

    } catch (e: IOException) {
        return null
    }
}


fun JacksonHelper.serializeAsPrettyString(value: Any, compatibleWeb: Boolean = false): String {
    val mapper = if(compatibleWeb) JacksonHelper.webCompatibilityMapper else JacksonHelper.defaultObjectMapper
    return  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
}

fun <R> String.letIfNotBlank(block: (String) -> R): R? {
    if(this.isNotBlank())
    {
        return block(this)
    }
    return null
}


fun <R> String?.runIfNotNullOrBlank(block: String.() -> R): Unit {
    if(!this.isNullOrBlank())
    {
        block(this)
    }
}


