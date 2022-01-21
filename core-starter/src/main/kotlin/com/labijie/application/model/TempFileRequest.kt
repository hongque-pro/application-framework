package com.labijie.application.model

import javax.validation.constraints.NotBlank

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-06
 */
class TempFileRequest {
    @get:NotBlank(message = "文件扩展名不能为空")
    var fileExtensions:String = ""

    @get:NotBlank(message = "文件夹路径不能为空")
    var folder:String = ""

    @get:NotBlank(message = "文件类型不能为空")
    var fileType: String = ""
}