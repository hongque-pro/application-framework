# 全渠道通用支付模块

当前版本支持的支付平台： 微信支付、支付宝支付

支持的支付方式 (PaymentMethod): 小程序

### 1. 引入包：
---
```groovy
dependencies {
    compile("com.labijie.application:framework-payment-starter:$package-version")
}
```
### 2. 添加配置：
---
```yaml
application:
  pay:
    callback-base-url: [回调的域名，例如：https://mydoamin.com]
    providers:
      alipay:
        app-id: [小程序, 第三方应用或其他应用的 app id]
        app-secret: [小程序, 第三方应用或其他应用私钥]
        app-account: [收款的支付宝商户号]
        alipay-pub-key: [小程序, 第三方应用或其他应用的支付宝公钥]
      wechat:
        app-id: [小程序, 公众号或其他应用的 appid, ISV 模式必须使用服务号的 appid]
        app-secret: [小程序, 公众号或其他应用的 key, ISV 模式必须使用服务号的 key]
        merchantKey: [收款商户号的 api key]
        sub-app-id: [仅 ISV 模式有效，实际付款用户使用的小程序, 公众号或其他应用的 appid]
```
> 所有配置均在 idea 中有 AutoComplete 提示，例子中仅展示重要的配置，其他配置所用默认值即可。
>
> **注意**：配置中的 callback-base-url 仅需要指定回调的 mvc 服务对外暴露的基础路径。
>
> 例如：如果你将支持回调的 mvc 程序通过 nginx 映射到 https://xxx.com/api , 则 callback-base-url 必须配置为 https://xxx.com/api （https://xxx.com/api/ 与 https://xxx.com/api 等效，结尾的 / 不会产生任何副作用）。


## 向第三方平台下单（调用各平台统一收单 API）

 ### 注入 PayUtils 并调用 createTrade 方法, 以下以微信为例 ：
 
 

```kotlin
    @Autowired
     private lateinit var paymentUtils: PaymentUtils

    fun foo(){
        val trade = PaymentTrade().apply{
             //设置参数
        }
        if(testEnabled) {
            val result = this.paymentUtils.createTrade(WechatPaymentOptions.ProviderName, trade)
            println(JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result))
        }
    }

```

>注意： 请勿使用硬编码的方式来指定 payment provider 名称，可以通过各平台配置类上的静态字段获取：
>
>微信： WechatPaymentOptions.ProviderName
>
>支付宝：AlipayPaymentOptions.ProviderName

**下单参数**： 下单入参 PaymentTrade 属性说明 （各属性均会进行数据校验，请仔细阅读下表特别说明）：

> 非必填参数保持默认值即可

|属性名称| 默认值 | 必填 | 说明 | 特别说明 |
| ------ | ------ | ------ | -----|  -----|
| tradeId | 0 | 是 | 业务系统的交易号 | 
| mode | ISV | 否 | ISV收单或普通收单 |
| platformMerchantKey | null | 特殊 | ISV 模式下实际收单商户 | 仅 ISV 模式有效，对于支付宝为 app_auth_token, 对于微信为 sub_mch_id |
| amount | 0 | 是 | 支付金额（单元：元） |
| platformBuyerId | null | 是 | 付款用户的平台账号 | 微信要使用配置的应用对应的 openid |
| timeoutMinutes | 1440（24小时） | 否 | 付款超时时间（分钟） | 支付宝最长不得超过 15 小时，超过 15 小时取 15 小时 |
| method | MiniProgram | 否 | 支付方式 | 当前仅支持小程序 |
| allowCreditCard | true | 否 | 是否允许信用卡支付 | 支付宝无效 |
| subject | null | 是 | 交易摘要 | 最大不能超过 128 个字符 |
| platformParameters | map(0) | 否 | 提交到平台的额外参数 | 需参考各平台文档填入参数，不建议使用 |
| state | null | 否 | 自定义数据，可在回调时接收 | 长度不能超过64个字符串, 并且只支持小写字母、数字、减号(-)、下划线(_) |
| scene | null | 否 | 场景数据 | 依赖 provider 来实现该支持，详细信息参见[支付场景](payment-scene.md)支持 |

**返回值**

|属性名称| 说明 | 特别说明 |
| ------ | ------ | ------ |
| tradeId | 业务系统的交易号 |  |
| paymentProvider | 本次调用使用的 provider |  |
| platformTradeId | 第三方平台的交易号 | 微信返回 null, 支付宝返回支付宝交易单号 |
| deductMode | 扣款方式 | 用户主动付款/代扣 |
| hasCallback | 是否有支付成功回调 | 大多数情况返回 true, 有一些例外，例如支付宝停车场景为 false，返回 false 可以调用查询 API 查询订单状态 |
| tag | 对于特殊流程需要的附加数据， 下单时使用了不同的 method 则该值具有不同的值 | 微信小程序： 返回了所有需要的字段（包含签名），可供前端直接使用 ， 参考文档： https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_7&index=5 ; 支付宝无任何返回值，因为支付宝后续处理仅需要 platformTradeId 值 |

> 建议将该返回值直接给到前端，前端自行处理后续流程

## 处理支付回调

framework-payment-starter 包提供了支付回调自动处理程序开关，默认并不开启，需要开启回调处理逻辑，通过如下方式：

### 1. 使用注解开启回调处理支持
---
```kotlin
@SpringBootApplication
@EnablePaymentCallbackHandling //此处开启了支付回调处理程序
class DummyApplication

fun main(args: Array<String>) {
    runApplication<DummyApplication>(*args)
}
```

注意: 支付回调处理程序仅支持 Spring Mvc, 而且必须手动引入 core-web 包：

>对于非 web 程序（例如一个消息队列消费进程），你可能不需要具有 web 功能，因此 core-web 不是必须项，需要的时候手动引入即可。

```kotlin

dependencies {
    compile("com.labijie.application:framework-core-web:$package-version")
}

```



### 2. 向 Spring 上下文中注入实现 IPaymentCallbackHandler 接口的 Bean , 在 handleCallback 方法中处理的回调的业务逻辑。
---
回调处理方法中 PaymentCallbackContext 中的 request （PaymentCallbackRequest 类型）是用于处理业务的关键属性。

**特别说明**： 

IPaymentCallbackHandler 无需进行验签等和第三方平台的操作，第三方平台的交互已由框架完成，回调发生时表示已经进行过验签等处理动作，handleCallback 方法专注于业务即可。

如果仅处理回调的而没有下单操作无需配置 application.pay.callback-base-url ，但是你仍然需要配置 application.pay 的其他内容，因为回调程序需要根据配置进行验签。



例子：

```kotlin
@Component
class MyPaymentCallbackHandler: IPaymentCallbackHandler {
    override fun handleCallback(context: PaymentCallbackContext) {
        // 这里处理自己的业务逻辑
    }
}
```

PaymentCallbackRequest 属性说明：


|属性名称| 说明 | 特别说明 |
| ------ | ------ | ------ |
| appid | 下单时的 appid |  |
| tradeId | 业务系统的交易号 |  |
| platformTradeId | 第三方平台的交易号 |  微信此处为真正的交易单号，可以用于后续的查询与其他 API 调用，建议存储 |
| status | 付款状态 |  AndClose 结尾的状态标识用户已不可更改（不可退款）  |
| timePaid | 用户付款时间 |  |
| amount | 订单金额 | 业务系统应该核对订单金额，如果不一致可以选择不做任何处理或抛出异常  |
| platformBuyerId | 第三方平台的付款用户 id | 对于微信为 appid 对应的 openid, 不同的微信应用不相同 |
| originalPayload | 第三方平台回调携带的原始数据 | 非特殊情况不建议使用  |

> handleCallback 如果没有抛出异常会认为处理成功，框架会通知第三方平台不再进行回调，因此，对于业务逻辑失败，必须在 handleCallback 中抛出异常

**注意**： handleCallback 方法必须幂等，无需考虑任何异常处理（框架会自动处理异常），如果处理失败直接抛出异常即可。

## 其他

尽可能通过注入 PaymentUtils 来处理支付相关逻辑，PaymentUtils 类提供了获取 provider 的方法，业务代码应该避免直接使用 IPaymentProvider 接口。

由于第三方平台对于自定义单号要求必须唯一，因此建议业务系统实现 订单和付款记录的一对多关系，以支持对一张订单多次付款的场景（微信支付失败、允许支付宝支付）


## 从第三方平台查询支付交易

使用方法：
```kotlin
val query = PaymentTradeQuery(result.tradeId, isPlatformTradeId = false, mode = TradeMode.ISV).apply {
                this.platformMerchantKey = parameters.platformMerchantKey
            }

val queryResult = this.paymentUtils.queryTrade(WechatPaymentOpetions.ProviderName, query)
```

PaymentTradeQuery 参数说明：

|属性名称| 说明 | 特别说明 |
| ------ | ------ | ------ |
| id | 业务系统交易单号或第三方平台单号 | 由 isPlatformTradeId 决定该单号的类型，应该优先使用第三方平台单号 |
| isPlatformTradeId | 是否使用第三方平台的交易单号 |  |
| platformMerchantKey | ISV 模式下实际收单商户 |  仅 ISV 模式有效 |

返回值 PaymentTradeQueryResult 说明：

|属性名称| 说明 | 非空(YES/NO) | 特别说明 |
| ------ | ------ | ------ | ---- |
| tradeId | 业务系统的交易单号 | Y | |
| platformTradeId | 第三方平台的交易单号 | 支付宝 Y;微信 N  | 微信在 NotPay 状态时无返回值，即必须买家付款后才产生该值 |
| platformBuyerId | 第三方平台的买家账号 |  Y | 微信在 NotPay 状态时无返回值，即必须买家付款后才产生该值 |
| amount | 付款金额 |  Y |  |
| status | 标准交易状态 |  Y |  |
| platformStatus | 第三方平台交易状态代码 |  Y | 不建议使用 |
| paidTime | 付款时间 |  N | 支付宝不返回该值，微信仅在买家付款后才返回该值。判断状态过于麻烦，因此不建议使用，回调时所有平台都能返回这个值，建议该值回调时保存 |

## 对于特殊行业的支付场景支持

支付宝、微信在不通行业可能有特殊的支付 API， 基础的支付结构不足以和这些 API 匹配，但是可以通过场景数据扩展支持场景支持。

具体参考：[支付场景](payment-scene.md)