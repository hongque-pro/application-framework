package com.labijie.application


/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
@Suppress("ConstPropertyName")
object ApplicationErrors {
    @ErrorDescription("服务器系统错误")
    @ErrorDescription("An internal server error has occurred", locale = Constants.EN_US)
    const val UnhandledError = "system_error"

    @ErrorDescription("HTTP 请求中未包含正文 ( body )")
    @ErrorDescription("HTTP request is missing body", locale = Constants.EN_US)
    const val HttpBodyIsMissing  = "http_body_is_missing"

    @ErrorDescription("知道不到符合此请求的终结点")
    @ErrorDescription("No endpoint matches this request", locale = Constants.EN_US)
    const val BadRequestMethod  = "bad_request_method"

    @ErrorDescription("请求参数不满足约束条件")
    @ErrorDescription("Request parameters do not meet constraints", locale = Constants.EN_US)
    const val BadRequestParameter  = "bad_request_param"

    @ErrorDescription("请求数据的格式不正确或请求数据中的字段格式不正确")
    @ErrorDescription("Request format is invalid or fields are incorrectly formatted", locale = Constants.EN_US)
    const val InvalidRequestFormat  = "invalid_request_format"

    @ErrorDescription("缺少必要的请求参数")
    @ErrorDescription("Missing required request parameters", locale = Constants.EN_US)
    const val RequestParameterMissed  = "miss_request_param"

    @ErrorDescription("验证码不正确")
    @ErrorDescription("Incorrect verification code", locale = Constants.EN_US)
    const val InvalidVerificationCode = "invalid_verification_code"

    @ErrorDescription("手机号格式不正确")
    @ErrorDescription("Invalid phone number format", locale = Constants.EN_US)
    const val InvalidPhoneNumber = "invalid_phone_number"

    @ErrorDescription("用户名格式不正确")
    @ErrorDescription("Invalid username format", locale = Constants.EN_US)
    const val InvalidUsername = "invalid_username"

    @ErrorDescription("名称格式不正确")
    @ErrorDescription("Invalid display name format", locale = Constants.EN_US)
    const val InvalidDisplayName = "invalid_display_name"

    @ErrorDescription("电子邮件地址格式不正确")
    @ErrorDescription("Invalid email address format", locale = Constants.EN_US)
    const val InvalidEmailAddress = "invalid_email_address"

    @ErrorDescription("电子邮件地址尚未验证")
    @ErrorDescription("Email address has not been verified", locale = Constants.EN_US)
    const val EmailAddressNotVerified = "email_address_not_verified"

    @ErrorDescription("手机号码尚未验证")
    @ErrorDescription("Phone number has not been verified", locale = Constants.EN_US)
    const val PhoneNumberNotVerified = "phone_number_not_verified"

    @ErrorDescription("安全令牌已过期或不正确，可能由于你操作花费的时间太长")
    @ErrorDescription("Security token expired or invalid, possibly due to timeout", locale = Constants.EN_US)
    const val InvalidSecurityStamp = "invalid_security_stamp"


    @ErrorDescription("操作太过频繁，请稍后再试")
    @ErrorDescription("Operation too frequently, please try again later", locale = Constants.EN_US)
    const val OperationOutOfRateLimit = "operation_too_frequently"

    @ErrorDescription("存储的文件不存在或已经被删除")
    @ErrorDescription("Stored file does not exist or has been deleted", locale = Constants.EN_US)
    const val StoredObjectNotFound = "stored_object_not_found"

    @ErrorDescription("数据已经被其他人修改，刷新页面")
    @ErrorDescription("Data has been modified by others, please refresh", locale = Constants.EN_US)
    const val DataMaybeChanged = "data_maybe_changed"

    @ErrorDescription("你没有权限编辑或操作该数据")
    @ErrorDescription("You do not have permission to edit or operate on this data", locale = Constants.EN_US)
    const val DataOwnerMissMatched = "data_owner_unmatched"

    @ErrorDescription("数据不存在或已经被删除")
    @ErrorDescription("Data does not exist or has been deleted", locale = Constants.EN_US)
    const val DataMissed = "data_missed"

    @ErrorDescription("数据已经存在")
    @ErrorDescription("Data already exists", locale = Constants.EN_US)
    const val DataExisted = "data_existed"

    @ErrorDescription("数据操作冲突，请重试")
    @ErrorDescription("Data operation conflict, please try again", locale = Constants.EN_US)
    const val DataOperationConcurrency = "data_operation_concurrency"

    @ErrorDescription("不支持的 HTTP 媒体类型")
    @ErrorDescription("Unsupported HTTP media type", locale = Constants.EN_US)
    const val MediaTypeNotAcceptable = "media_type_not_acceptable"

    @ErrorDescription("你没有权限进行该操作")
    @ErrorDescription("You do not have permission to perform this operation", locale = Constants.EN_US)
    const val PermissionDenied  = "permission_denied"

    @ErrorDescription("密码至少 8 位，必须包含字母、数字和至少一位非数字字母字符。")
    @ErrorDescription("Password must be at least 8 characters long and contain letters, numbers, and at least one special character", locale = Constants.EN_US)
    const val StrongPasswordConstraintViolation = "strong_password_constraint_violation"

    @ErrorDescription("人机验证失败，请重试")
    @ErrorDescription("Human-machine verification failed, please try again", locale = Constants.EN_US)
    const val RobotDetected = "robot_detected"

    @ErrorDescription("数据验签失败")
    @ErrorDescription("Data signature verification failed", locale = Constants.EN_US)
    const val InvalidSignature = "invalid_signature"

    @ErrorDescription("该账号不存在")
    @ErrorDescription("Account does not exist", locale = Constants.EN_US)
    const val UserNotFound = "usr_not_found"

    @ErrorDescription("文件索引已经存在")
    @ErrorDescription("File index already exists", locale = Constants.EN_US)
    const val FileIndexAlreadyExisted  = "file_index_existed"

    @ErrorDescription("文件不存在")
    @ErrorDescription("File does not exist", locale = Constants.EN_US)
    const val FileIndexNotFound  = "file_index_not_found"

    @ErrorDescription("临时文件已经超时")
    @ErrorDescription("Temporary file has expired", locale = Constants.EN_US)
    const val TemporaryFileTimout  = "temporary_file_timout"

}
