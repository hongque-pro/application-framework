## 支付宝小程序和微信小程序增强支持

**从 TwoFactorPrincipal 获取登录渠道**

TwoFactorPrincipal.isWechatMiniUser 用于判断当前用户是否是微信小程序用户

TwoFactorPrincipal.isAlipayMiniUser 用于判断当前用户是否是支付宝小程序用户

TwoFactorPrincipal.loginProvider 获取当前社交网络登录提供程序 （返回值： mini-alipay, mini-wechat， 空串）

> mini-alipay, mini-wechat 常量位于 AlipayMiniOptions.ProviderName 和 WechatMiniOptions.Provider 静态属性上

增强 Spring Security 支持微信、支付宝小程序用户访问权限

例子：

```kotlin
@Configuration
class MyModuleAutoConfiguration : IResourceAuthorizationConfigurer {
    override fun configure(registry: ResourceAuthorizationRegistry) {
        registry
            .antMatchers("/webchartSomthing").wechatMiniOnly()
            .antMatchers("/alipaySomthing").alipayMiniOnly()
    }
}
```