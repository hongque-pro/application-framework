package com.labijie.application.core.testing

import com.labijie.application.crypto.RsaUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-12
 */
class RsaUtilsTester {
    private val privateKey2048 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC/bNdBSOo8kTB92Mghyy4GIRmT8oyu1NmQWHuyPwMxI27d477bPW1U1o4HQaDCIoVqoLolM5tw5F9d5XExXrEKsEXLFevWudTCXwp+cbM1JPJarIW4oY47LFdXgdRlsbOSjDsrX5IBL28oeomt45SFf/LlSAWRwfXhVh4TKp4r8J6Bzbj8Sb7F5gbbOUL25ApXvX2Pb5ESoHwnN6XWcshZpqtxKMhjLuKnIJo1Kfdl2FesdLx/ryiOdutlYihdpt3eHp/ftl+vkCDFifTW87WxGWk62NVuO1kzFsSsLzpKm7HRB20Iq1diSOdZD+Wvo66ZJpajwOYorYedRL/yYU0fAgMBAAECggEAVd7mZPPnl8ntvdZ8WKSDfd5lUffxYhA7lMQMoTYhIW7qsgETQdg5bmBdECZBjjTcMqvtNPkElszrdvyRHXuD4e6XMHYh+I4eQh9OXtB0erebPE++IMfgPO00NR4ZgJEJpAjiFh/mKD/2Fmt5zZaUrppqBK6dSPiAa1gtX52znLOowm7aybWCF3ryDyAA0b4/tytWMFvC7bjl7Ynd9niDhuJOoXkqTdfiEiAdy9yxEa1pGAF3p/3CNIY7ie7k4QdHEMGMjoIV9AGfxuo4FTPEC52dhXgDju5gkBVk3DSWSNXHzjSIVQHdU5IG7GOrzQmBheqK4zhkIRBe0kP8VZPj2QKBgQD7RM514JyLBOBJsAxA22EH6gdvQO4+jeC/pSHM9zsWDi5sXBWVZKg/i3Bvnwu0yhA/neapBglNBZhkDKAVzjJIlJYJ5CLIdjxcaf0XQMxdtajDeKgO83WhrgwOvHlcc42qRclqRMDL3dvDDy4f/GuGvmeHjmKNKxVpzhsy0tDodQKBgQDDB5HNrcXqpMGzSx2LMAJfyit+nREyzy9AUGIUU2JHC9xfaFK5cTkhRDHyoWG+/vWEB5nuKIo+VKUXdYHl1oA1DG81a/Hf4X7a4URWp+SLTXGzL2qPVWzbv64urFfYKuj/mAW12EO6JwGWcadkxr1RoXdKhlu2/u5MX3Xpy5fMwwKBgQCXP1aru/Ve9SNxF6h4jdU9Z1DsVgBS5SIpGeRbtWH6p7SoA1H2qC1ierPGbM0mGQ9oy8phacHFfB2786GVHpZD/DTIZWvOkOVKl34CNj4OFmAssX8v6LqhBNw7LpY64U+f8/wGnLYUwUocgxG3rUCoTgqRnVKTOSDdnHPgxud7VQKBgADZHpQzZkHv93utHvLFAa2i9m91hWagKgdwkgyelDXU4l9z17BAwsvXPRReCUvIVg797XhwqrE8sv8YHrl+Jk6UwEMONh4QH/oZsq9hyYeljZIG6cK3z2JhQgOn8jXZcevyoEu7JghANSyPT/gLOTWKqzciwHFaSHUCkUO0A/P7AoGAcj4fclpNKwpy85btghmGvvYB5qDAx/c3QfizoxQ3FAkxsOHerr+JrAkjo6rhDM9rmVcsGF85tkXWp1iyQWwZ0okvHKUiV19vMU6RfJCplQsVuMNd196AfKm8eD/1fdu/ncAkAHwa3aCUs31P+3JXGEhtd6tjXtdvxUJQIHlyFGc="
    private val publicKey2048 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv2zXQUjqPJEwfdjIIcsuBiEZk/KMrtTZkFh7sj8DMSNu3eO+2z1tVNaOB0GgwiKFaqC6JTObcORfXeVxMV6xCrBFyxXr1rnUwl8KfnGzNSTyWqyFuKGOOyxXV4HUZbGzkow7K1+SAS9vKHqJreOUhX/y5UgFkcH14VYeEyqeK/Cegc24/Em+xeYG2zlC9uQKV719j2+REqB8Jzel1nLIWaarcSjIYy7ipyCaNSn3ZdhXrHS8f68ojnbrZWIoXabd3h6f37Zfr5AgxYn01vO1sRlpOtjVbjtZMxbErC86Spux0QdtCKtXYkjnWQ/lr6OumSaWo8DmKK2HnUS/8mFNHwIDAQAB"

    private val privateKey1024 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKOl1TumLlafe5c3BHI5F7rzxn9mSVm7gGmsqJq46kW8bXqzxDphfcvJZv02K4KeoxQ2feXx6PYbFqF7ewb0Gu7MvHFvnhg5vwnsRhyTV8nUZdu1h7XeE/ecoF9zPY3JNiZkrvJDbah8yDqtuPru7pQnjbVJa/EanXuphHytO68jAgMBAAECgYEAiFhjp7YWh4+K/nsVVkGhNt9is8S6OmwRWnpDY93LOtz6bjAWvvl5QPAFwdR/Pe4EO5QHIy2BhoMdQN/Lqgh54r9jua0S8idS/sicoX0vr2qIRGjRF+uB3u2VldenHZlHhS2sni5fjbJ8pOei2oglFtF5w1Nx890MPB3qciogvQECQQD2s9pjcLftBq7JgjWvV450PMruy45t0X2qOChRWYERvy9skSej8E8SAHxKPP6FY7YlaK75BQ0xlUwY9/PsKqSjAkEAqdCuQr4SoqJWvWx+75XIslg57jpDdn8Hjrhe5yGnZhVHsDFaJNogoGGHxICTO6ZMlNKhwuGzfRYKCcAhKCjzgQJAEANwo4/HgI00f0QCdBU82/KuQX3ZCuvUOl5Wz6D9rcc5LtTlO9D3cPWzG6aF5uaoBhmbcfKP/q3wOJavsJ67/QJBAKH/ab5SBQURSR47unxkr6kzcVBKVMFePZ9xU1e7Sy6fblwvOIefbzCQ2iwjXFGzO6tdpON83PWfaMQUfY0KRAECQCzCsXYGrFtpKGgjd/pCInS7g1M/YdBd3vAdqVfPhtCAftFGvI1I9g6lZZCbf0yRUXgz5q0JJcKS/VqzdgOk8oc="
    private val publicKey1024 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjpdU7pi5Wn3uXNwRyORe688Z/ZklZu4BprKiauOpFvG16s8Q6YX3LyWb9NiuCnqMUNn3l8ej2Gxahe3sG9BruzLxxb54YOb8J7EYck1fJ1GXbtYe13hP3nKBfcz2NyTYmZK7yQ22ofMg6rbj67u6UJ421SWvxGp17qYR8rTuvIwIDAQAB"

    @Test
    fun signSha256(){
        val content = mapOf(
            "a" to "123",
            "c" to "afddsfdsf",
            "b" to "1234"
        )

        val sign = RsaUtils.rsaSignSHA256(content, privateKey2048)

        val valid = RsaUtils.verifySHA256(content, sign, publicKey2048)

        Assertions.assertTrue(valid)
    }

    @Test
    fun signSha1(){
        val content = mapOf(
            "a" to "123",
            "c" to "afddsfdsf",
            "b" to "1234"
        )

        val sign = RsaUtils.rsaSignSHA1(content, privateKey2048)

        val valid = RsaUtils.verifySHA1(content, sign, publicKey2048)

        Assertions.assertTrue(valid)
    }

    @ParameterizedTest
    @ValueSource(strings = [ "racecar", "radar", "able was I ere I saw elba" ])
    fun encrypt1024(content:String){
        val s = RsaUtils.encrypt(content, publicKey1024)
        val d = RsaUtils.decrypt(s, privateKey1024)

        Assertions.assertEquals(content, d)
    }

    @ParameterizedTest
    @ValueSource(strings = [ "racecar", "radar", "able was I ere I saw elba" ])
    fun encrypt2048(content:String){
        val s = RsaUtils.encrypt(content, publicKey2048)
        val d = RsaUtils.decrypt(s, privateKey2048)

        Assertions.assertEquals(content, d)
    }
}