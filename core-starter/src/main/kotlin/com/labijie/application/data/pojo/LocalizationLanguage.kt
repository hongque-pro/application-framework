@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`data`.pojo

import kotlin.Boolean
import kotlin.String

/**
 * POJO for LocalizationLanguageTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.data.LocalizationLanguageTable
 */
public open class LocalizationLanguage {
  public var locale: String = ""

  public var language: String = ""

  public var country: String = ""

  public var disabled: Boolean = false
}
