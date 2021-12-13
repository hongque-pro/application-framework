package com.labijie.application

import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.Charset


class QueryStringEncoder @JvmOverloads constructor(uri: String?, private val charset: Charset = Charsets.UTF_8) {
    private val uriBuilder: StringBuilder = if (!uri.isNullOrBlank()) StringBuilder(uri) else StringBuilder()
    private var hasParams = false

    /**
     * Adds a parameter with the specified name and value to this encoder.
     */
    fun addParam(name: String, value: String?) {
        if (hasParams) {
            uriBuilder.append('&')
        } else {
            uriBuilder.append('?')
            hasParams = true
        }
        encodeComponent(name)
        if (value != null) {
            uriBuilder.append('=')
            encodeComponent(value)
        }
    }

    private fun encodeComponent(s: CharSequence) {
        encodeNonUtf8Component(s)
    }

    /**
     * Returns the URL-encoded URI object which was created from the path string
     * specified in the constructor and the parameters added by
     * [.addParam] method.
     */
    @Throws(URISyntaxException::class)
    fun toUri(): URI {
        return URI(toString())
    }

    /**
     * Returns the URL-encoded URI which was created from the path string
     * specified in the constructor and the parameters added by
     * [.addParam] method.
     */
    override fun toString(): String {
        return uriBuilder.toString()
    }

    /**
     * Encode the String as per RFC 3986, Section 2.
     *
     *
     * There is a little different between the JDK's encode method : [URLEncoder.encode].
     * The JDK's encoder encode the space to `+` and this method directly encode the blank to `%20`
     * beyond that , this method reuse the [.uriBuilder] in this class rather then create a new one,
     * thus generates less garbage for the GC.
     *
     * @param s The String to encode
     */
    private fun encodeNonUtf8Component(s: CharSequence) {
        //Don't allocate memory until needed
        var buf: CharArray? = null
        var i = 0
        val len = s.length
        while (i < len) {
            var c = s[i]
            if (dontNeedEncoding(c)) {
                uriBuilder.append(c)
                i++
            } else {
                var index = 0
                if (buf == null) {
                    buf = CharArray(s.length - i)
                }
                do {
                    buf[index] = c
                    index++
                    i++
                } while (i < s.length && !dontNeedEncoding(s[i].also { c = it }))
                val bytes: ByteArray = String(buf, 0, index).toByteArray(charset)
                for (b in bytes) {
                    appendEncoded(b.toInt())
                }
            }
        }
    }

    /**
     * @see ByteBufUtil.writeUtf8
     */
    private fun encodeUtf8Component(s: CharSequence) {
        var i = 0
        val len = s.length
        while (i < len) {
            val c = s[i]
            if (!dontNeedEncoding(c)) {
                encodeUtf8Component(s, i, len)
                return
            }
            i++
        }
        uriBuilder.append(s)
    }

    private fun encodeUtf8Component(s: CharSequence, encodingStart: Int, len: Int) {
        if (encodingStart > 0) {
            // Append non-encoded characters directly first.
            uriBuilder.append(s, 0, encodingStart)
        }
        encodeUtf8ComponentSlow(s, encodingStart, len)
    }

    private fun encodeUtf8ComponentSlow(s: CharSequence, start: Int, len: Int) {
        var i = start
        while (i < len) {
            val c = s[i]
            if (c.toInt() < 0x80) {
                if (dontNeedEncoding(c)) {
                    uriBuilder.append(c)
                } else {
                    appendEncoded(c.toInt())
                }
            } else if (c.toInt() < 0x800) {
                appendEncoded(0xc0 or (c.toInt() shr 6))
                appendEncoded(0x80 or (c.toInt() and 0x3f))
            } else if (c.isSurrogate()) {
                if (!Character.isHighSurrogate(c)) {
                    appendEncoded(WRITE_UTF_UNKNOWN.toInt())
                    i++
                    continue
                }
                // Surrogate Pair consumes 2 characters.
                if (++i == s.length) {
                    appendEncoded(WRITE_UTF_UNKNOWN.toInt())
                    break
                }
                // Extra method to allow inlining the rest of writeUtf8 which is the most likely code path.
                writeUtf8Surrogate(c, s[i])
            } else {
                appendEncoded(0xe0 or (c.toInt() shr 12))
                appendEncoded(0x80 or (c.toInt() shr 6 and 0x3f))
                appendEncoded(0x80 or (c.toInt() and 0x3f))
            }
            i++
        }
    }

    private fun writeUtf8Surrogate(c: Char, c2: Char) {
        if (!Character.isLowSurrogate(c2)) {
            appendEncoded(WRITE_UTF_UNKNOWN.toInt())
            appendEncoded(if (c2.isHighSurrogate()) WRITE_UTF_UNKNOWN.toInt() else c2.toByte().toInt())
            return
        }
        val codePoint = Character.toCodePoint(c, c2)
        // See https://www.unicode.org/versions/Unicode7.0.0/ch03.pdf#G2630.
        appendEncoded(0xf0 or (codePoint shr 18))
        appendEncoded(0x80 or (codePoint shr 12 and 0x3f))
        appendEncoded(0x80 or (codePoint shr 6 and 0x3f))
        appendEncoded(0x80 or (codePoint and 0x3f))
    }

    private fun appendEncoded(b: Int) {
        uriBuilder.append('%').append(forDigit(b shr 4)).append(forDigit(b))
    }

    companion object {
        private const val WRITE_UTF_UNKNOWN = '?'.toByte()
        private val CHAR_MAP = "0123456789ABCDEF".toCharArray()

        /**
         * Convert the given digit to a upper hexadecimal char.
         *
         * @param digit the number to convert to a character.
         * @return the `char` representation of the specified digit
         * in hexadecimal.
         */
        private fun forDigit(digit: Int): Char {
            return CHAR_MAP[digit and 0xF]
        }

        /**
         * Determines whether the given character is a unreserved character.
         *
         *
         * unreserved characters do not need to be encoded, and include uppercase and lowercase
         * letters, decimal digits, hyphen, period, underscore, and tilde.
         *
         *
         * unreserved  = ALPHA / DIGIT / "-" / "_" / "." / "*"
         *
         * @param ch the char to be judged whether it need to be encode
         * @return true or false
         */
        private fun dontNeedEncoding(ch: Char): Boolean {
            return ch in 'a'..'z' || ch in 'A'..'Z' || ch in '0'..'9' || ch == '-' || ch == '_' || ch == '.' || ch == '*'
        }
    }
}