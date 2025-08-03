package com.labijie.application.hcaptcha.testing

import com.labijie.application.hcaptcha.HCaptchaService
import com.labijie.application.hcaptcha.configuration.HCaptchaProperties
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * @author Anders Xiao
 * @date 2025/8/3
 */
class HCaptureTester {

    val token: String = "P1_eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.haJwZACjZXhwzmiPEX-ncGFzc2tlecUDmI8jy6oBqQ3L0yEmyWuQjEIEysqkel1186GpZ8C-MfnsmUOubFhXsvH6XCng6jowtn_Ok1-Pmj9GwRJuWQ6zUDWJZ0Ye2lKMrG_RAh-emqQsbdPkOt3isHo_EPuZOspf8fESSFpRfDR6SsOPnL-C6AHyNeNhlNT-3pdUb4woIcN08I8IBj8eKymzorwX73z4V6sQaK-4kkTeJOWiHyx33pfbVG8YYODHHCaAzn7618hUS15ED0b2slKcHt83XWrJbwjIDZ7bEMetfWwBzZ9Q14tPG2kyrijBzDvT6WBqQOF_0FpLO7MDMi1zoJy4tg9yGi5yreLNx36G97agRXgMYrMD6F70VoONGd8bOF4eMcoVu2OIF_lcOfrZjzzo9XfY5xM76KS60jHt3txIQL6DNWugpzJLYslhqaOxG47_jsQ-s-Sa6sTzkd1BvHMcyOYvaZob1Cou4Afi4mFaN71LUntdxUV1PTEy4TIGh-UDMq_Y-bu5dRb5ErsI36LgyjfOXqq-vxOSvKLTiNLnZ62jaTaBBOeLDEXrsbb61qzHY7bZx-qV3RzJwnu8torCN3le9GxQ_m-kWewQFNSAikNOqMN1YMnv9172BGp3HWgo1IPe_xuyCnWniJdDr0CERgIqe4G4wmkm2Egiwga6SaOR6T7YE7bUeqshruaxeXuyj1GE8irsq03P1G6uSueRjh2FabhAo3iwDPlxGR1RDr_x2G-75rk46nVWBmfRoB6uUMbgayoETe4KSbwsVnMjTEMf4q0L7yN0r8SheAiWwA6VUtera1QrHGujYKq5WXqiLHK1SYqv8LhaF3yBuVyqy4lMmdwwU4GDkCTkOs3JlcDRhO3pcsaOpw3KqFhFZQ9x1YbakCv0xCH1G4oxjGa_fsXgVY_pq53bUvhOBS0a-zFHoGJMalH06l-qt0wAneqetkDvZ6-PvyA8mU3cLtZ5gZ7Nh3W4urmqQOgg_P65raOxtuGnhADAMMq20Fg6b-LK79d2nGympQTMads6J7-uHh_lwYMU2JylIwL03VenIkwVeHRJDKukDo_YUf9xv0mE7GsilAsGnBX8fv1XMA7UhuQUaxWJ5VUeIQeKzZsUdFqSow2CDbRY-L7zoHAHF7a7tij1vUm6fbw_xaUSEqCPIXjIO0_n-XDNsLcHy19Y624MFCBAQcAB5ebCSHXkNAo4i4-25McV4bFCE9H8YFj9gFjuQrbsN0GAqEIxomtyp2Y5ZjE2ZjKoc2hhcmRfaWTOFZnkVA.q4q-BNLB9oWQHVbjMHwFsU9FDO72ctjOfZM_B4o2WWw"
    val secretValue:  String = "ES_278e01d786ae4ffda4b70f784d73b37e"

    private val service = HCaptchaService(HCaptchaProperties().also {
        it.secret = secretValue
        it.logError = true
    }, null)

    @Test
    fun testVerify() {
        service.check(token, null, null)
    }
}