# 订单工作流模块

订单，特别是支付的流程因为存在第三方调用和数据库双写的场景，需要谨慎的处理乐观并发，防止订单状态覆盖、重复付款等问题，该模块旨在重用订单下单、付款的工作流程，避免出现难以调试的 online bug。

## 如何使用

### 1. 引入 jar 包

```groovy
dependencies {
    compile("com.labijie.application:framework-order-starter:$package-version")
}
```

> 该包自动包含了[支付模块](payment.md), 使用订单默认使用支付模块。

### 2. 实现自定义的订单适配器

- #### 2.1 实现自定订单

```kotlin
class MyOrder {
    var id:Long = 0
    var subject: String = ""
}
```

- #### 2.2 实现自定义订单适配器，适配到订单工作流的标准化订单模型

对于每一种自定义的订单类型，实现一个适配器 Bean：

```kotlin
@Component
class MyOrderAdapter() : IOrderAdapter<MyOrder> {
    override val orderType: KClass<MyOrder> 
    get() = MyOrder::class
    
    override fun adaptOrder(order: MyOrder): NormalizedOrder {
        //省略代码...
    }

    override fun getOrderById(orderId: Long): MyOrder? {
        //省略代码...
    }

    override fun effectPayment(orderId: Long, value: PaymentEffect, currentStatus: PaymentStatus): Boolean {
        //省略代码...
    }

    override fun effectPayment(
        orderId: Long,
        value: PaymentEffect,
        minStatus: PaymentStatus?,
        statusMax: PaymentStatus?
    ): Boolean {
        //省略代码...
    }
}

```

> 注意
> effectPayment 方法中的 status 参数表示乐观并发条件，通常数据库实现为：
>
> update set value = values where id = [orderId] and status = [currentStatus]
>
> 重载方法中的 minStatus 和 maxStatus 至少会具有一个值, 表示区间判断，取值使用: minStatus.code/statusMax.code

### 3. 发起支付流程

```kotlin
    @Autowired
     private lateinit var orderWorkflow: IOrderWorkflow

    fun foo(){
        //省略参数构造代码...
        val model: OrderAndPayment orderWorkflow.beginPayment(input, MyOrder::class, allowPayWithCreatedStatus = false)
         val order = model.orderSnapshot
    }

```

说明：

调用 beginPayment 将返回返回一个包含订单快照（适配器 getByOrderId 的调用结果）和第三方支付调用结果的对象，如果调用失败将会抛出 OrderException, 一般情况下无需处理异常。

> 注意： 快照表示该订单对象仅仅是操作过程中的对象，当调用结束后数据库中 order 值可能已经改变；事实上 orderSnapshot 中的支付状态已经被更新，如果需要数据库当前值，业务需要重新查询数据库。

OrderException 常见类型简单说明：

|异常类型|说明|
|---|---|
|OrderAdapterNotFoundException| 找不到指定类型的订单适配器，请确保你实现了 orderType 参数的对应适配器。|
|OrderAlreadyClosedException| 关闭的订单不能发起支付流程。|
|OrderInvalidPaymentStatusException| 通常由于发起支付的订单发生了并发，同一张单只能发起一次支付（状态 >=PAYING 不能再次支付） |
|OrderNotFoundException| getByOrderId 实现的方法没有找到订单 |
|PaymentTradeNotFoundException|付款交易找不到，正常情况不应该发生，调用完成支付流程时如果没有付款单产生此异常|

> 所有异常类型遵循框架异常处理模式，一般情况下无需做任何处理。


### 4. 完成支付流程

- #### 自动完成

在 payment (具体参考 [支付模块](payment.md)) 支付回调可用的情况下， order-starter 自动 hook 支付回调（通过 IPaymentCallbackHandler 实现 ）自动在支付回调时完成订单支付流程。

自动完成情况下，如果需要自定义处理流程，可以通过 OrderPaymentCallbackHandledEvent （ Spring 事件 Pub/Sub 编程模型）事件进行后续自定义处理，如果 Subscription 为同步处理，可以通过抛出异常的方式告知支付回调不成功，则支付平台后续会再次重试回调。

- ####  手动完成

特殊情况可以手动完成订单支付流程：

```kotlin

fun foo(){
        val result = PaymentResult() 
        //省略参数构造代码...
        val orderSnapshot: MyOrder = orderWorkflow.endPayment(result, MyOrder::class)
    }

```

该调用同样返回一个订单快照，快照的含义参见支付流程。

注意：

PaymentResult 对象中的 platformBuyerId （有值的情况下） 和 amount 会和数据库中的交易单进行校验，如果校验失败会抛出 PaymentTradeNotFoundException 异常

### 5. 如何在允许 Payment 回调的情况下关闭自动完成

默认情况如果支付回调可用（通过 EnablePaymentCallbackHandling ）, 会自动 hook 订单完成流程，如果需要关闭以手工处理，通过添加如下配置：

```yaml
application.order.callback-handler.enabled: false
```

