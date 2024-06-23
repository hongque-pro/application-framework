@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo

import kotlin.Boolean
import kotlin.Byte
import kotlin.Long
import kotlin.Short
import kotlin.String

/**
 * POJO for UserTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserTable
 */
public class User {
  public var userName: String = ""

  public var userType: Byte = 0

  public var accessFailedCount: Short = 0

  public var concurrencyStamp: String = ""

  public var email: String = ""

  public var emailConfirmed: Boolean = false

  public var language: String = ""

  public var lockoutEnabled: Boolean = false

  public var lockoutEnd: Long = 0L

  public var passwordHash: String = ""

  public var phoneCountryCode: Short = 0

  public var phoneNumber: String = ""

  public var phoneNumberConfirmed: Boolean = false

  public var securityStamp: String = ""

  public var timeZone: String = ""

  public var twoFactorEnabled: Boolean = false

  public var approved: Boolean = false

  public var approverId: Long = 0L

  public var timeExpired: Long = 0L

  public var timeLastLogin: Long = 0L

  public var timeLastActivity: Long = 0L

  public var timeCreated: Long = 0L

  public var lastSignInIp: String = ""

  public var lastSignInPlatform: String = ""

  public var lastSignInArea: String = ""

  public var lastClientVersion: String = ""

  public var id: Long = 0L
}
