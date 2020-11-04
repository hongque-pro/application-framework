# 支付场景

通过可扩展场景支持，让基础支付包支持各平台的特殊支付 API。

平台特殊支付 API 有两种：
1. 在统一下单 API 中插入不同的参数；
2. 全新的 API，但是大部分出参和入参相同、基础的验签、传输协议相同；

对于情况 1： 实现 ISceneSupport 接口来支持；对于情况2：  通过基础结构实现新的 IPaymentProvider，最大限度重用代码；

## 车主服务支付场景包使用
---
**引入包**：

```groovy
dependencies {
    compile("com.labijie.application:framework-payment-car-starter:$package-version")
}
```

下单时为 PlatformTrade 对象设置 **scene** 属性。

### 普通支付流程中扩展的场景：

|scene 类型|说明|微信支持|支付宝支持|特别说明|
|----|----|----|----|----|
|CarUserGuideScene|普通支付过程中授权开通车主服务场景|Y|N||

> 这些场景依赖于 payment-starter 中的 payment provider，仅是对这些 provider 的额外扩展。
>
>  provider 不支持的场景数据会自动忽略，不需要为特殊平台判断，可以无脑传入，没有任何副作用。

调用时 createTrade 时加入场景数据：

```kotlin

class CarPaySample{
    @Autowired
    private lateinit var paymentUtils: PaymentUtils

    fun foo(){
        val carUserGuideScene = CarUserGuideScene(carNumber = "沪A30P01")
        
        val trade = PlatformTrade(
            //其他参数省略...

            //场景数据
            scene =carUserGuideScene
        )
        
        paymentUtils.createTrade(WechatPaymentOptions.ProviderName, trade) 
    }
}

```

### 车主平台代扣费下单：

这类场景使用全新的 provider 实现，底层重用了 payment-starter 中的大量基础代码。

面向车主平台的 payment provider:

|provider name|说明| provider name取值|
|----|----|----|
|wechat-car|微信车主服务支付|CarPaymentProviders.Wechat|
|alipay-car|支付宝车主服务支付|CarPaymentProviders.Alipay|

> hasCallback 为 false 时，可以通过交易查询方法查询交易结果，如果查询结果仍不确定，业务需要自行轮询（平台设计缺陷，无能为力）

由于 provider 名称太多，建议业务系统定义如下结构方便编码：

```kotlin
object PaymentProviderNames {
    const val Wechat = WechatPaymentOptions.ProviderName
    const val Alipay = AlipayPaymentOptions.ProviderName
    const val WechatCar = CarPaymentProviders.Wechat
    const val AlipayCar = CarPaymentProviders.Alipay
    //其他....
}
```


|scene 类型|说明|wechat-car 支持| alipay-car 支持|
|----|----|----|----|
|CarParkingScene|各平台车主服务代扣收款场景|Y|Y|

CarParkingScene 数据属性说明:

> Y 为必填，N为选填，-- 为忽略

|属性|说明|wechat-car |alipay-car |特殊说明
|----|----|----|----|----|
|carNumber|车牌号|Y|Y|
|platformParkingId|支付平台的停车场 id|--|Y|
|timeEnter|入场 UNIX 时间戳|Y|--|
|timeExit|出场 UNIX 时间戳|Y|--|
|parkingName|停车场名称|Y|--|
|deductMode|主动支付/代扣|Y|--|

> 微信需要根据用户状态来判断 deductMode 取值，当前版本不提供自动取值，因此必须手工填写。
>
> 业务不应该避免在代码中出现根据平台来设置参数属性的做法，参数都应该全部填写，让 provider 自动忽略无效属性。

#### 支付宝（alipay-car）回调问题

支付宝代扣没有支付回调支持:

业务不应该根据平台来判断

而是通过返回值 PaymentTradeCreationResult.hasCallback 属性判断是否有回调支持

当 hasCallback 为 **false** 时，调用主动查询（queryTrade）来获得交易状态，如果查询接口出错无法获取到状态，要通过定时任务调度来轮询（支付平台设计缺陷，无能为力）。







