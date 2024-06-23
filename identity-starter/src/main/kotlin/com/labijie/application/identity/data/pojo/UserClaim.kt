@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo

import kotlin.Long
import kotlin.String

/**
 * POJO for UserClaimTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserClaimTable
 */
public class UserClaim {
  public var claimType: String = ""

  public var claimValue: String = ""

  public var userId: Long = 0L

  public var id: Long = 0L
}
