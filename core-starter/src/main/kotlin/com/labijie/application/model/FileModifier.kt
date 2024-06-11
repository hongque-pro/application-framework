/**
 * @author Anders Xiao
 * @date 2023-12-11
 */
package com.labijie.application.model

import com.labijie.application.BucketPolicy


enum class FileModifier {
    Public,
    Private
}

fun FileModifier.toObjectBucket(): BucketPolicy {
    return if(this == FileModifier.Private) BucketPolicy.PRIVATE else BucketPolicy.PUBLIC
}