package com.labijie.application.open.test

import com.labijie.application.crypto.DesUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.Duration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-09
 */
class DESUtilsTester {

    @Test
    fun encryptWithDefaultKey(){
        val txt = "DSFSDF#@#$#$#"
        val encrypted  = DesUtils.encrypt(txt)
        val txt2 = DesUtils.decrypt(encrypted)

        Assertions.assertEquals(txt, txt2)
    }

    @ParameterizedTest
    @ValueSource(strings = [ "racecar", "radar", "able was I ere I saw elba" ])
    fun encryptWithAnyKey(){
        val txt = "DSFSDF#@#$#$#"
        val encrypted  = DesUtils.encrypt(txt, "abc123")
        val txt2 = DesUtils.decrypt(encrypted, "abc123")

        val txt3 = DesUtils.decrypt(encrypted, "1234567890")

        Assertions.assertEquals(txt, txt2)
        Assertions.assertNotEquals(txt, txt3)
    }

    @ParameterizedTest
    @ValueSource(strings = [ "racecar", "radar", "able was I ere I saw elba" ])
    fun tokenWithAnyKey(key: String){
        val token = DesUtils.generateToken("abc",
            Duration.ofMinutes(1), key)
        val valid = DesUtils.verifyToken(token, "abc", key)

        Assertions.assertTrue(valid)
    }

    @Test
    fun tokenWithDefaultKey(){
        val token = DesUtils.generateToken("abc", Duration.ofMinutes(1))
        val valid = DesUtils.verifyToken(token, "abc")

        Assertions.assertTrue(valid)
    }


    @Test
    fun tokenWithBadModifier(){
        val token = DesUtils.generateToken("abc", Duration.ofMinutes(1))
        val valid = DesUtils.verifyToken(token, "bcd")

        Assertions.assertFalse(valid)
    }

    @Test
    fun tokenExpired(){
        val token = DesUtils.generateToken("abc", Duration.ofMillis(100))
        Thread.sleep(200)
        val valid = DesUtils.verifyToken(token, "abc")

        Assertions.assertFalse(valid)
    }
}