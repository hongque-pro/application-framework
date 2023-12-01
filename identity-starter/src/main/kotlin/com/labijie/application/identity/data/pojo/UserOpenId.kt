@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo

import kotlin.Long
import kotlin.String

/**
 * POJO for UserOpenIdTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserOpenIdTable
 */
public open class UserOpenId {
  public var userId: Long = 0L

  public var appId: String = ""

  public var loginProvider: String = ""

  public var openId: String = ""
}
