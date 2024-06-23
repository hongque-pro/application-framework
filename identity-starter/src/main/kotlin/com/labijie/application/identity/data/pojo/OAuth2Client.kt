@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo

import kotlin.Boolean
import kotlin.Int
import kotlin.String

/**
 * POJO for OAuth2ClientTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.OAuth2ClientTable
 */
public class OAuth2Client {
  public var clientId: String = ""

  public var resourceIds: String = ""

  public var clientSecret: String = ""

  public var scopes: String = ""

  public var authorizedGrantTypes: String = ""

  public var redirectUrls: String = ""

  public var authorities: String = ""

  public var additionalInformation: String = ""

  public var autoApprove: Boolean = false

  public var enabled: Boolean = false

  public var accessTokenLiveSeconds: Int = 0

  public var refreshTokenLiveSeconds: Int = 0

  public var authorizationCodeLiveSeconds: Int = 0

  public var deviceCodeLiveSeconds: Int = 0

  public var reuseRefreshTokens: Boolean = false
}
