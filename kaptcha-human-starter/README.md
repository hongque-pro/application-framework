### 后端使用
#### 1. 加入基础依赖
```groovy
dependencies {
  compile "com.labijie.application: framework-kaptcha-human-starter:$framework_version"
}
```

#### 2. 在需要验证的controller上面加入注解，例如：
```kotlin
@HumanVerify
@PostMapping("/captcha")
fun sendSmsCaptcha(
    @RequestParam("p", required = false) numberOrUserId: String? = null,
    @RequestParam("t", required = false) captchaType: CaptchaType? = CaptchaType.General,
    @RequestParam("m", required = false) modifier: String? = null
): SimpleValue<String> {
/* ...... */
}
```

### 前端使用：

#### 1. 调用获取验证码图片
```
GET /kaptcha/gen?userKey=xxx

RESPONSE:
{
    "stamp": "xxx",  // 下面验证使用
    "img": "xxx"  // base64编码的图片
}
```
> `userKey` 可随机生产，下面验证时候使用


#### 2. 在需要验证的请求里面带上头
```
h-token: "stamp;captcha;userKey"
```
> 其中`captcha`为用户输入的验证码

若验证失败会返回`http code`403:
```
{
    "error": "robot_detected",
    "errorDescription": "robot detected."
}
```