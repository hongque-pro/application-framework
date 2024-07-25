package com.labijie.application


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@Suppress("ConstPropertyName")
object ApplicationErrors {
    @ErrorDescription("服务器系统错误")
    const val UnhandledError = "system_error"
    @ErrorDescription("请求参数不满足约束条件")
    const val BadRequestParameter  = "bad_request_param"
    @ErrorDescription("请求数据的格式不正确或请求数据中的字段格式不正确")
    const val InvalidRequestFormat  = "invalid_request_format"
    @ErrorDescription("缺少必要的请求参数")
    const val RequestParameterMissed  = "miss_request_param"
    @ErrorDescription("验证码不正确")
    const val InvalidCaptcha = "invalid_captcha"
    @ErrorDescription("手机号格式不正确")
    const val InvalidPhoneNumber = "invalid_phone_number"
    @ErrorDescription("用户名格式不正确")
    const val InvalidUsername = "invalid_username"
    @ErrorDescription("名称格式不正确")
    const val InvalidDisplayName = "invalid_display_name"
    @ErrorDescription("电子邮件地址格式不正确")
    const val InvalidEmailAddress = "invalid_email_address"
    @ErrorDescription("安全令牌已过期或不正确，可能由于你操作花费的时间太长")
    const val InvalidSecurityStamp = "invalid_security_stamp"
    @ErrorDescription("发送短信太过频繁，请稍后再试")
    const val SmsTooFrequentlyException = "sms_out_of_limit"
    @ErrorDescription("存储的文件不存在或已经被删除")
    const val StoredObjectNotFound = "stored_object_not_found"
    @ErrorDescription("数据已经被其他人修改，刷新页面")
    const val DataMaybeChanged = "data_maybe_changed"
    @ErrorDescription("你没有权限编辑或操作该数据")
    const val DataOwnerMissMatched = "data_owner_unmatched"
    @ErrorDescription("数据不存在或已经被删除")
    const val DataMissed = "data_missed"
    @ErrorDescription("数据已经存在")
    const val DataExisted = "data_existed"
    @ErrorDescription("数据操作冲突，请重试")
    const val DataOperationConcurrency = "data_operation_concurrency"
    @ErrorDescription("不支持的 HTTP 媒体类型")
    const val MediaTypeNotAcceptable = "media_type_not_acceptable"
    @ErrorDescription("你没有权限进行该操作")
    const val PermissionDenied  = "permission_denied"

    @ErrorDescription("密码至少 8 位，必须包含字母、数字和至少一位非数字字母字符。")
    const val StrongPasswordConstraintViolation = "strong_password_constraint_violation"

    //人机验证失败
    @ErrorDescription("人机验证失败，请重试")
    const val RobotDetected = "robot_detected"

    @ErrorDescription("数据验签失败")
    const val InvalidSignature = "invalid_signature"

    @ErrorDescription("该账号不存在")
    const val UserNotFound = "usr_not_found"

    @ErrorDescription("文件索引已经存在")
    const val FileIndexAlreadyExisted  = "file_index_existed"

    @ErrorDescription("文件不存在")
    const val FileIndexNotFound  = "file_index_not_found"

    @ErrorDescription("临时文件已经超时")
    const val TemporaryFileTimout  = "temporary_file_timout"
}
