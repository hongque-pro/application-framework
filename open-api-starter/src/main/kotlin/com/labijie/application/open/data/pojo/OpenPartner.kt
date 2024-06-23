@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`open`.`data`.pojo

import com.labijie.application.`open`.model.OpenPartnerStatus
import kotlin.Int
import kotlin.Long
import kotlin.String

/**
 * POJO for OpenPartnerTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.open.data.OpenPartnerTable
 */
public class OpenPartner {
  public var name: String = ""

  public var timeExpired: Long = 0L

  public var status: OpenPartnerStatus = OpenPartnerStatus.DISABLED

  public var timeLatestPaid: Long = 0L

  public var timeLatestUpdated: Long = 0L

  public var phoneNumber: String = ""

  public var contact: String = ""

  public var email: String = ""

  public var appCount: Int = 0

  public var id: Long = 0L
}
