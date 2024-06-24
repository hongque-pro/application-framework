@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`open`.`data`.pojo

import com.labijie.application.`open`.model.OpenAppStatus
import kotlin.Byte
import kotlin.Long
import kotlin.String

/**
 * POJO for OpenAppTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.open.data.OpenAppTable
 */
public open class OpenApp {
  public var displayName: String = ""

  public var appSecret: String = ""

  public var appType: Byte = 0

  public var signAlgorithm: String = ""

  public var jsApiKey: String = ""

  public var jsApiDomain: String = ""

  public var logoUrl: String = ""

  public var status: OpenAppStatus = OpenAppStatus.DISABLED

  public var partnerId: Long = 0L

  public var timeCreated: Long = 0L

  public var timeConfigUpdated: Long = 0L

  public var concurrencyStamp: String = ""

  public var configuration: String = ""

  public var id: Long = 0L
}
