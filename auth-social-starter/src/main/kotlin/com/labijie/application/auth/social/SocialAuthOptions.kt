package com.labijie.application.auth.social

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-12-10
 */
 abstract class SocialAuthOptions{
    var appId:String = ""
    var appSecret: String = ""
    var exchageUrl:String = ""
    var providerName:String = ""
    protected set

    /**
     * 是否不同的 appid 具有不同的 open id，例如微信，应该配置为 true
     */
    abstract  val multiOpenId:Boolean

   var sandboxPhoneNumber: String = ""

    open val isSandbox:Boolean
      get() = false
}