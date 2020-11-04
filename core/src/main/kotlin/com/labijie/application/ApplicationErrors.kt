package com.labijie.application


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
object ApplicationErrors {
    @ErrorDescription("服务器系统错误")
    const val UnhandledError = "system_error"
    @ErrorDescription("请求参数不满足约束条件")
    const val BadRequestParameter  = "bad_request_param"
    @ErrorDescription("缺少必要的请求参数")
    const val RequestParameterMissed  = "miss_request_param"
    @ErrorDescription("验证码不正确")
    const val InvalidCaptcha = "invalid_captcha"
    @ErrorDescription("手机号格式不正确")
    const val InvalidPhoneNumber = "invalid_phone_number"
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
    @ErrorDescription("不支持的 HTTP 媒体类型")
    const val MediaTypeNotAcceptable = "media_type_not_acceptable"
    @ErrorDescription("你没有权限进行该操作")
    const val PermissionDenied  = "permission_denied"

    //人机验证失败
    @ErrorDescription("人机验证失败，请重试")
    const val RobotDetected = "robot_detected"

    @ErrorDescription("数据验签失败")
    const val InvalidSignature = "invalid_signature"

    @ErrorDescription("该账号不存在")
    const val UserNotFound = "usr_not_found"
}
