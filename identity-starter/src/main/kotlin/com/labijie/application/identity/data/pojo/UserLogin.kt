@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo

import kotlin.Long
import kotlin.String

/**
 * POJO for UserLoginTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserLoginTable
 */
public open class UserLogin {
  public var loginProvider: String = ""

  public var providerKey: String = ""

  public var providerDisplayName: String = ""

  public var userId: Long = 0L
}
