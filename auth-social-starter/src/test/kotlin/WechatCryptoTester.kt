import com.labijie.application.auth.social.providers.wechat.WechatCrypto
import org.junit.jupiter.api.Assertions
import kotlin.test.Test

/**
 * @author Anders Xiao
 * @date 2023-11-30
 */

class WechatCryptoTester {
    @Test
    fun testSha1(){
        val sessionKey = "HyVFkGl5F5OQWJZZaNzBBg=="
        val data = """
            {"nickName":"Band","gender":1,"language":"zh_CN","city":"Guangzhou","province":"Guangdong","country":"CN","avatarUrl":"http://wx.qlogo.cn/mmopen/vi_32/1vZvI39NWFQ9XM4LtQpFrQJ1xlgZxx3w7bQxKARol6503Iuswjjn6nIGBiaycAjAtpujxyzYsrztuuICqIM5ibXQ/0"}
        """.trimIndent()
        val result = WechatCrypto.verifySha1(data.trim(), sessionKey, "75e81ceda165f4ffa64f4068af58c64b8f54b88c")

        Assertions.assertTrue(result)
    }
}