# Application Framework

最大限度业务代码复用，为开始一个模块化应用做好准备 ！

# 约定模式（最佳实践）：

所有业务模块（Module）以 spring boot starter 形式出现，一个 starter 中包含：
1. 业务模型 model
2. 业务抽象的服务 service
3. 用于描述 api 接口的 controller

设计约定： 
1. 一个程序具有一个 shell 来启动模块，需要启动的模块以依赖形式加入
2. 数据库所有字段不可为 NULL
3. 数据模型尽可能不要暴露到业务层，而是使用该文档中的模型映射技术自己定义

## 模块开发

加入基础依赖 
```groovy
dependencies {
  compile "com.labijie.application: framework-core-web: $framework_version"
}
```

不需要 web 功能的模块

```groovy
dependencies {
  compile "com.labijie.application: framework-core: $framework_version"
}
```

>  建议项目自己创建 commons 包，所有模块依赖 commons 从而间接依赖框架

### 模块自动集成接口

模块通过几个约定的轻量级接口实现统一的编程模型，starter 中存在实现这些接口的 bean， 框架完成自动加载。

#### IModuleInitializer 接口

IModuleInitializer 接口，该接口一个 module 中出现一次，表示该模型需要全局初始化的动作.

IModuleInitializer接口不包含任何方法签名，约定使用无返回值的 initialize 方法，参数可以注入任意的 bean 进行初始化。

例子：

```kotlin
@Component
class DataModuleInitializer : IModuleInitializer {
    fun initialize(
        mybean: MyBean,
        txManager: PlatformTransactionManager) {

        DataUtils.txManager = txManager

    }
}
```

#### IResourceAuthorizationConfigurer 接口

IResourceAuthorizationConfigurer 用于 web 模块的权限配置，通过 spring security 的配置方式来保护模块的 API 接口。

> 因为 ResourceServerConfigurerAdapter 有无法调和的顺序问题，所有使用一层薄的抽象来解决。
> 通常 IResourceAuthorizationConfigurer 接口实现再具有 @Configuration 注解的 AutoConfiguration 类即可。

通常， 
例子：

```kotlin
@Configuration
@ComponentScan("com.labijie.project.mymodule")
class MyModuleAutoConfiguration : IResourceAuthorizationConfigurer {
    override fun configure(registry: ResourceAuthorizationRegistry) {
        registry
            .antMatchers("/somthing/**").authenticated()
            .antMatchers(
                "/my/**").permitAll()
    }
}
```

## 异常处理

异常统一定义为字符串常量，

内置异常定义例子：

```kotlin
object ApplicationErrors {
    const val UnhandledError = "system_error"
    const val BadRequestParameter  = "bad_request_param"
    const val RequestParameterMissed  = "miss_request_param"
    const val RequestParameterBadFormat  = "bad_format_request_param"
    const val InvalidCaptcha = "invalid_captcha"
    const val InvalidSecurityStamp = "invalid_security_stamp"
    const val SmsTooFrequentlyException = "sms_out_of_limit"
    const val StoredObjectNotFound = "stored_object_not_found"
    const val DataMaybeChanged = "data_maybe_changed"
    const val DataOwnerMissMatched = "data_owner_unmatched"
    const val DataMissed = "data_missed"

    const val PremiumCalcFault = "premium_calc_fault"
    //人机验证失败
    const val ROBOT_DETECTED = "robot_detected"
}
```


注册你错误代码, 创建一个实现了 IErrorRegistration 接口得 bean （该 bean 每个模块一个）：

```kotlin
@Component
class AppErrorsRegistration : IErrorRegistration {
    override fun register(registry: IErrorRegistry) {
        registry.registerErrors(ApplicationErrors)
    }
}
```

也可以通过注解定义

```kotlin
@Component

@ErrorDefine(classes = [AppErrorsRegistration::class]) //定义错误代码
@Configuration(proxyBeanMethods = false)
class MyAutoConfiguration : IErrorRegistration
```

> <mark>注意：</mark> 使用注解定义异常代码时，异常代码类必须声明为 `object`

为已知的异常代码定义异常类型：

```kotlin
class DataMaybeChangedException(message:String? = null)
    : ErrorCodedException(ApplicationErrors.DataMaybeChanged, message?:"Data currently being edited may have been changed.") {
}
```

遵循以上约定定义的异常代码可以通过 /application-errors 路径来查看

> 继承自 ErrorCodedException 的异常框架自动在 mvc 层处理为一致的返回格式。

## 枚举的处理

枚举在所有语言中几乎都是数字值的语意，为了方便跨语言（如 typescript， C# 等）交互我们认为枚举值都是一个数字，由于 java 枚举设计的复杂性，原生 java 枚举不具备跨语言能力，因此，枚举必须通过约定方式来定义以消除语言特性，框架支持两种模式的枚举.

> 如果发现需要字符串枚举，字符串常量 + display 函数是更好的选择。

### 数值连续性枚举

通过简单定义，以 ordinal 作为枚举实际值。
例如：

```kotlin
enum class SimpleStatus {
    DISABLED,
    ACTIVE,
}
```

### 非连续性枚举

通过实现 IDescribeEnum<> 接口框架可以自动处理枚举在各个层次的转换，以 code 作为枚举值.

例如：

```kotlin
enum class OrderStatus(override val code: Byte, override val description: String) : IDescribeEnum<Byte> {
    PENDING(0, "提交/待审核"),
    WAITING_PAY(10, "等待支付"),
    PAID(15, "已支付"),
    PAID_CONFIRMED(18, "支付已确认"),
    IN_FORCE(20, "已生效")
}
```

业务层尽可能使用枚举来定义模型，模型中不应该存在 int 、byte 之类的表示枚举的字段。
框架可以自动处理业务层枚举和 mybatis 生成的数值类型字段之间的映射。
前端通过数字值来接收，框架会将 api 返回值自动处理成数字值，对编程透明。

## 不同层次模型的映射

业务模型可能和数据库映射的模型仅仅只是字段多少的差异，通常不需要更改字段名，为此框架提供搞效的 ”copy“ 工具简化代码：

考虑如下数据模型 :

```kotlin
class ProductTable {
  var id: Long
  var status: Byte
  var name: String
}
``` 

有业务模型
```kotlin
class ProductModel {
  var id: Long = 0
  var status: ProductStatus = ProductStatus.Enabled
}
``` 

> 其中 ProductStatus 为上述枚举章节方式定义的枚举

从数据库模型映射到业务层模型： 

```kotlin

val tableRecord:ProductTable = .... //省略

val product = ProductModel().propertiesFrom(tableRecord)
```

该例子的关注点：

1. 字段名称相同，自动映射 
2. 自动处理枚举类型  （ProductModel 的 status 字段是一个枚举，而 ProductTable 是 Byte）
3. 自动丢弃多余字段 （ProductModel 中不存在 name 字段）

**需要注意的关键点：**

java 的 Long 和 kotlin 中的 Long ，同样的问题还有（short , byte 等）并不是同一种类型，但是上述例子依然可以自动处理，并遵循如下规则：

如果要复制的对象是 java 对象使用了 Long， 并且字段的值为 null， kotlin 对象如果是 Long （非空），映射后得到的是 -1，
所有的 null 包装类型会映射为 kotlin 的非空类型 -1. 但是反向映射，会将 -1 传递给 Long，因此，业务应该使用 <=0 来作为空值语义。

> BeanCopierUtils.copyProperties 类是 propertiesFrom 扩展方法的背后实现

## API 返回值格式约定

正常情况下，返回值格式（除了 auth ， 因为 auth 是 spring 代码，如果干预，对 spring 入侵太多）应该统一，不应该出现 string 和 json 混用的情况。

对于简单类型，我们通过包装 SimpleValue 使其变成 json 对象。


## 游标式分页帮助器

游标式分页约定返回值类型为 ForwardList 

其中游标 token 生成的使用 ForwardTokenCodec 帮助器类生成，复杂例子：

```kotlin
  getInsuranceOrders(filter: InsuranceOrderFilter?, forwardToken: String?, pageSize: Int): ForwardList<InsuranceOrderEntry> {
        val example = InsuranceCatalogExample().apply {
            this.orderByClause = "${InsuranceCatalog.Column.modifiedAt.desc()}, ${InsuranceCatalog.Column.id.desc()}"
            if(filter != null){
                //... filter 查询过滤代码省略，关注游标分页


                if(forwardToken != null){
                    val offset = ForwardTokenCodec.decode(forwardToken)
                     //排序字段大小比较
                    c.andModifiedAtLessThanOrEqualTo(offset.offsetValue.toLong())
                     //排除主键                 
                    if(offset.excludeKeys.isNotEmpty()) {
                        if (offset.excludeKeys.size == 1) {
                            c.andIdNotEqualTo(offset.excludeKeys.first())
                        } else {
                            c.andIdNotIn(offset.excludeKeys)
                        }
                    }
                }
            }
        }
        val list = this.insuranceCatalogMapper.selectByExampleWithRowbounds(example, RowBounds(0, pageSize))
        val mapped = list.map { InsuranceOrderEntry().propertiesFrom(it) }
        val token = if(list.size >= pageSize) ForwardTokenCodec.encode(list,
            {it.modifiedAt}, {it.id}) else null
        return ForwardList(mapped, token)
    }
```

**forwardToken 解析（ForwardTokenCodec.decode）：**

1. 上述例子中使用 modifiedAt 字段倒序，该字段并不是主键。
2. forwardToken 由服务器给到前端，前端查询下一页时需要带回来
3. forwardToken 解析后得到 ForwardOffset 对象包含两个字段offsetValue 是排序字段的偏移量, 本例为 modifiedAt 字段 ， excludeKeys 是需要排除的主键（通常为空）


**forwardToken 生成 （ForwardTokenCodec.encode）：**

```kotlin
ForwardTokenCodec.encode(list, {it.modifiedAt}, {it.id})
```

> 其中第一个参数为这一次的查询结果，第二个参数为排序字段表达式，第三个参数为主键字段表达式

上述代码使用访问操作表达式书写可能更为清晰， 等价代码如下:

```kotlin
ForwardTokenCodec.encode(list, InsuranceCatalog:: modifiedAt, InsuranceCatalog::id)
```

 > encode 后两个参数语义，告诉我排序字段， 告诉我主键字段





## 其他

### 人机验证

对于需要人机验证的接口，使用 @HumanVerify 注解即可

### HTTP 缓存

对于需要 HTTP 缓存的接口，使用 @HttpCache 注解即可

### controller 中获取当前用户

直接使用 TwoFactorPrincipal 类型的参数在方法上接收即可（如果没有登录会返回 auth 错误） 

### 短信邮件处理

直接注入 IMessageSender 使用即可（IMessageSender.sendSmsCaptcha 使用异步模式, 调试时可以考虑使用同步模式）

### 富文本跨站脚本防御

使用 @XxsReject 注解验证的字段即可

### 可配置正则验证

使用 @ConfigurablePattern 注解需要验证的字段即可。